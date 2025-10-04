import { http } from './http'
import axios from 'axios'

export async function getQuantity(productId: string) {
  try {
    const { data } = await http.get(`/inventories/${productId}`)
    return data?.data?.attributes?.quantity as number
  } catch (err) {
    if (axios.isAxiosError(err) && err.response?.status === 404) {
      // inventario no inicializado → mostrar 0 en UI
      return 0
    }
    throw err
  }
}

export async function decrement(productId: string, by = 1) {
  const { data } = await http.post(`/inventories/${productId}/decrement?by=${by}`, {})
  return data?.data?.attributes?.quantity as number
}
