import http.server
import socketserver
import os
import urllib.request
import urllib.error

PORT = 5173
DIRECTORY = os.path.dirname(os.path.abspath(__file__))
BACKEND_HOST = "https://www.justcatering.in/JCPortal"

class ProxyRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def do_GET(self):
        if self.path.startswith('/v1/api/'):
            self._proxy('GET')
        else:
            super().do_GET()

    def do_POST(self):
        if self.path.startswith('/v1/api/'):
            self._proxy('POST')
        else:
            self.send_error(405)

    def do_PUT(self):
        if self.path.startswith('/v1/api/'):
            self._proxy('PUT')
        else:
            self.send_error(405)

    def do_DELETE(self):
        if self.path.startswith('/v1/api/'):
            self._proxy('DELETE')
        else:
            self.send_error(405)

    def do_OPTIONS(self):
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', '*')
        self.end_headers()

    def _proxy(self, method):
        target_url = BACKEND_HOST + self.path
        content_length = int(self.headers.get('Content-Length', 0))
        body = self.rfile.read(content_length) if content_length > 0 else None
        
        req = urllib.request.Request(target_url, data=body, method=method)
        for key, val in self.headers.items():
            if key.lower() not in ('host', 'content-length'):
                req.add_header(key, val)

        try:
            with urllib.request.urlopen(req, timeout=12) as resp:
                self.send_response(resp.status)
                for k, v in resp.getheaders():
                    if k.lower() not in ('transfer-encoding', 'content-encoding', 'connection'):
                        self.send_header(k, v)
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
                self.wfile.write(resp.read())
        except urllib.error.HTTPError as e:
            self.send_response(e.code)
            for k, v in e.headers.items():
                if k.lower() not in ('transfer-encoding', 'content-encoding', 'connection'):
                    self.send_header(k, v)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()
            self.wfile.write(e.read())
        except Exception as e:
            self.send_response(502)
            self.send_header('Content-Type', 'application/json')
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()
            self.wfile.write(f'{{"success":false,"msg":"{str(e)}"}}'.encode('utf-8'))

    def end_headers(self):
        self.send_header('Cache-Control', 'no-store, no-cache, must-revalidate, max-age=0')
        self.send_header('Pragma', 'no-cache')
        self.send_header('Expires', '0')
        super().end_headers()

class ThreadingServer(socketserver.ThreadingMixIn, http.server.HTTPServer):
    daemon_threads = True
    allow_reuse_address = True

if __name__ == '__main__':
    with ThreadingServer(('0.0.0.0', PORT), ProxyRequestHandler) as httpd:
        print(f"Serving POS with live API proxy at http://localhost:{PORT}")
        httpd.serve_forever()
