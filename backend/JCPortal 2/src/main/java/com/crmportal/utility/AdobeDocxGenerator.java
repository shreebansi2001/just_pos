package com.crmportal.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AdobeDocxGenerator {

	@Autowired
	Environment environment;

	public String getAdobeAccessToken() {
		org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClients
				.createDefault();
		try {
			org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(
					"https://pdf-services.adobe.io/token");

			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			String body = "client_id=" + environment.getProperty("adobe.client-id") + "&client_secret="
					+ environment.getProperty("adobe.client-secret");
			post.setEntity(new org.apache.http.entity.StringEntity(body));

			org.apache.http.client.methods.CloseableHttpResponse response = httpClient.execute(post);
			try {
				String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());

				JSONObject json = new JSONObject(responseBody);

				return json.getString("access_token");
			} finally {
				response.close();
			}
		} catch (Exception e) {
			System.err.println("Token error: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String[] uploadPdfAsset(String accessToken, File pdfFile) {
		org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClients
				.createDefault();
		try {
			org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(
					"https://pdf-services.adobe.io/assets");

			post.setHeader("Authorization", "Bearer " + accessToken);
			post.setHeader("X-API-Key", environment.getProperty("adobe.client-id"));
			post.setHeader("Content-Type", "application/json");

			String body = "{\"mediaType\": \"application/pdf\"}";
			post.setEntity(new org.apache.http.entity.StringEntity(body));

			org.apache.http.client.methods.CloseableHttpResponse response = httpClient.execute(post);
			try {
				String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());
				System.out.println("Upload asset response: " + responseBody);

				JSONObject json = new JSONObject(responseBody);
				String assetID = json.getString("assetID");
				String uploadUri = json.getString("uploadUri");
				return new String[] { assetID, uploadUri };
			} finally {
				response.close();
			}
		} catch (Exception e) {
			System.err.println("Upload asset error: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean uploadPdfContent(String uploadUri, File pdfFile) {
		org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClients
				.createDefault();
		InputStream fileStream = null;
		try {
			org.apache.http.client.methods.HttpPut put = new org.apache.http.client.methods.HttpPut(uploadUri);

			put.setHeader("Content-Type", "application/pdf");

			fileStream = new FileInputStream(pdfFile);
			put.setEntity(new org.apache.http.entity.InputStreamEntity(fileStream, pdfFile.length()));

			org.apache.http.client.methods.CloseableHttpResponse response = httpClient.execute(put);
			try {
				int statusCode = response.getStatusLine().getStatusCode();

				return statusCode == 200;
			} finally {
				response.close();
			}
		} catch (Exception e) {
			System.err.println("Upload content error: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String submitExportJob(String accessToken, String assetID) {
		org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClients
				.createDefault();
		try {
			org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(
					"https://pdf-services.adobe.io/operation/exportpdf");

			post.setHeader("Authorization", "Bearer " + accessToken);
			post.setHeader("X-API-Key", environment.getProperty("adobe.client-id"));
			post.setHeader("Content-Type", "application/json");

			String body = "{" + "\"assetID\": \"" + assetID + "\"," + "\"targetFormat\": \"docx\"" + "}";
			post.setEntity(new org.apache.http.entity.StringEntity(body));

			org.apache.http.client.methods.CloseableHttpResponse response = httpClient.execute(post);
			try {
				int statusCode = response.getStatusLine().getStatusCode();

				org.apache.http.Header locationHeader = response.getFirstHeader("location");
				if (locationHeader != null) {
					return locationHeader.getValue();
				}
				return null;
			} finally {
				response.close();
			}
		} catch (Exception e) {
			System.err.println("Export job error: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String pollForResult(String accessToken, String jobLocation) {
		org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClients
				.createDefault();
		try {
			for (int i = 0; i < 30; i++) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				org.apache.http.client.methods.HttpGet get = new org.apache.http.client.methods.HttpGet(jobLocation);

				get.setHeader("Authorization", "Bearer " + accessToken);
				get.setHeader("X-API-Key", environment.getProperty("adobe.client-id"));

				org.apache.http.client.methods.CloseableHttpResponse response = httpClient.execute(get);
				try {
					String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());
					System.out.println("Poll response [" + i + "]: " + responseBody);

					JSONObject json = new JSONObject(responseBody);
					String status = json.getString("status");

					if ("done".equalsIgnoreCase(status)) {
						JSONObject asset = json.getJSONObject("asset");
						return asset.getString("downloadUri");
					} else if ("failed".equalsIgnoreCase(status)) {
						System.err.println("Job failed: " + responseBody);
						return null;
					}
				} finally {
					response.close();
				}
			}
			System.err.println("Job timed out after 60 seconds");
			return null;
		} catch (Exception e) {
			System.err.println("Poll error: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean downloadDocx(String downloadUri, String savePath) {
		org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClients
				.createDefault();
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			org.apache.http.client.methods.HttpGet get = new org.apache.http.client.methods.HttpGet(downloadUri);

			org.apache.http.client.methods.CloseableHttpResponse response = httpClient.execute(get);
			try {
				inputStream = response.getEntity().getContent();
				outputStream = new FileOutputStream(savePath);

				byte[] buffer = new byte[8192];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
				return true;
			} finally {
				response.close();
			}
		} catch (Exception e) {
			System.err.println("Download error: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String convertPdfToDocxAndGetUrl(File pdfFile, String eventNo) {

	    try {

	        if (pdfFile == null || !pdfFile.exists()) {
	            System.err.println("PDF file not found: " + pdfFile);
	            return null;
	        }

	        // Get Adobe access token
	        String accessToken = getAdobeAccessToken();
	        if (accessToken == null) {
	            System.err.println("Failed to get Adobe access token");
	            return null;
	        }

	        // Upload PDF asset
	        String[] uploadResult = uploadPdfAsset(accessToken, pdfFile);
	        if (uploadResult == null) {
	            System.err.println("Failed to upload PDF asset");
	            return null;
	        }

	        String assetId = uploadResult[0];
	        String uploadUri = uploadResult[1];

	        // Upload PDF content
	        if (!uploadPdfContent(uploadUri, pdfFile)) {
	            System.err.println("Failed to upload PDF content");
	            return null;
	        }

	        // Submit export job
	        String jobLocation = submitExportJob(accessToken, assetId);
	        if (jobLocation == null) {
	            System.err.println("Failed to submit export job");
	            return null;
	        }

	        // Poll until completed
	        String downloadUri = pollForResult(accessToken, jobLocation);
	        if (downloadUri == null) {
	            System.err.println("Failed to get conversion result");
	            return null;
	        }

	        // Save DOCX
	        String docxPath = pdfFile.getAbsolutePath()
	                .replaceFirst("(?i)\\.pdf$", ".docx");

	        if (!downloadDocx(downloadUri, docxPath)) {
	            System.err.println("Failed to save DOCX");
	            return null;
	        }

	        String docxFileName = new File(docxPath).getName();

	        return environment.getProperty("ws_image_path")
	                + "/api/download/docx/"
	                + eventNo + "/"
	                + docxFileName;

	    } catch (Exception e) {
	        System.err.println("PDF to DOCX conversion failed: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}
}
