import { productsApi } from "./http";

export async function listProducts(page = 1, size = 10) {
  const res = await productsApi.get("/products", {
    params: { "page[number]": page, "page[size]": size },
  });
  return res.data;
}
