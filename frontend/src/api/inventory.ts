import { http } from './http'


export async function getQuantity(productId: string) {
  const { data } = await http.get(`/inventories/${productId}`)
  return data?.data?.attributes?.quantity as number
}


export async function decrement(productId: string, by = 1) {
  const { data } = await http.post(`/inventories/${productId}/decrement?by=${by}`, {})
  return data?.data?.attributes?.quantity as number
}