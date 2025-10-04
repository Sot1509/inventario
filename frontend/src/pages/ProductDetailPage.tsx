import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { getProduct } from '../api/products'
import { decrement, getQuantity } from '../api/inventory'

export default function ProductDetailPage(){
  const { id = '' } = useParams()
  const qc = useQueryClient()

  const productQ = useQuery({ queryKey: ['product', id], queryFn: () => getProduct(id) })
  const qtyQ = useQuery({ queryKey: ['qty', id], queryFn: () => getQuantity(id), enabled: !!id })

  const buy = useMutation({
    mutationFn: (by: number) => decrement(id, by),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['qty', id] }) }
  })

  if (productQ.isLoading || qtyQ.isLoading) return <p className="p-4">Cargando…</p>
  if (productQ.error) return <p className="p-4 text-red-600">No se pudo cargar el producto.</p>

  const p: any = productQ.data
  const qty = qtyQ.data ?? 0
  const buying = buy.isPending

  // mensaje de error de compra
  const buyErrorMsg =
    (buy.error as any)?.response?.data?.errors?.[0]?.detail
    ?? (buy.error ? 'No se pudo comprar (stock insuficiente o error).' : '')

  return (
    <div className="container">
      <p><Link to="/">← Volver</Link></p>
      <h1>{p.name}</h1>
      <p className="muted">SKU: {p.sku}</p>
      <p>{p.description}</p>
      <p className="price">${p.price}</p>

      <p className="qty">
        Cantidad disponible: <strong>{qty}</strong>
      </p>
      {qty === 0 && (
        <p className="muted">Sin stock (aún no inicializado).</p>
      )}

      <div className="actions">
        <button disabled={buying || qty <= 0} onClick={() => buy.mutate(1)}>Comprar 1</button>
        <button disabled={buying || qty <= 1} onClick={() => buy.mutate(2)}>Comprar 2</button>
      </div>

      {buy.isError && <p className="text-red-600" role="alert">{buyErrorMsg}</p>}
    </div>
  )
}
