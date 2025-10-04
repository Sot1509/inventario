import { inventoryApi } from "./http";

export async function getInventory(productId: string) {
  const res = await inventoryApi.get(`/inventories/${productId}`);
  return res.data;
}

export async function decrementInventory(productId: string, by = 1) {
  const res = await inventoryApi.post(`/inventories/${productId}/decrement`, null, {
    params: { by },
  });
  return res.data;
}
