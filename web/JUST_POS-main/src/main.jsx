import "@/components/keenicons/assets/styles.css";
import "./styles/globals.css";
import axios from "axios";
import ReactDOM from "react-dom/client";
import { App } from "./App";
import { setupAxios } from "./auth";
import { ProvidersWrapper } from "./providers";
import React from "react";
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";

/**
 * Inject interceptors for axios.
 *
 * @see https://github.com/axios/axios#interceptors
 */
setupAxios(axios);
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <ProvidersWrapper>
    <App />
  </ProvidersWrapper>
);
// need to uncomment when production
// root.render(<React.StrictMode>
//     <ProvidersWrapper>
//       <App />
//     </ProvidersWrapper>
//   </React.StrictMode>);
