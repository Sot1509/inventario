import { http } from './http'
import { unwrapList, unwrapOne } from './jsonapi'

export type Product = {
  id: string
  sku: string
  name: string
  description?: string
  price: number
}

export async function listProducts(page = 1, size = 10) {
  const { data } = await http.get('/products', {
    params: { 'page[number]': page, 'page[size]': size }
  })
  return unwrapList<Omit<Product, 'id'>>(data)
}

export async function getProduct(id: string) {
  const { data } = await http.get(`/products/${id}`)
  return unwrapOne<Omit<Product, 'id'>>(data) as Product
}
