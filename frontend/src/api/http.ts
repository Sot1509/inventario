import axios from "axios";

export const productsApi = axios.create({
  baseURL: import.meta.env.VITE_PRODUCTS_URL,
  headers: {
    "X-API-KEY": import.meta.env.VITE_API_KEY,
    "Accept": "application/vnd.api+json",
    "Content-Type": "application/vnd.api+json",
  },
});

export const inventoryApi = axios.create({
  baseURL: import.meta.env.VITE_INVENTORY_URL,
  headers: {
    "X-API-KEY": import.meta.env.VITE_API_KEY,
    "Accept": "application/vnd.api+json",
    "Content-Type": "application/vnd.api+json",
  },
});
