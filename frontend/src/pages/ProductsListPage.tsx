import { useQuery } from '@tanstack/react-query'
import { Link, useSearchParams } from 'react-router-dom'
import { listProducts } from '../api/products'


export default function ProductsListPage(){
    const [sp, setSp] = useSearchParams()
    const page = Number(sp.get('page') ?? '1')
    const size = 10
    const { data, isLoading, error } = useQuery({ queryKey: ['products', page], queryFn: () => listProducts(page, size) })


    if (isLoading) return <p className="p-4">Cargando…</p>
    if (error) return <p className="p-4 text-red-600">Ocurrió un error al cargar los productos.</p>


    return (
        <div className="container">
        <h1>Productos</h1>
        <table className="table">
        <thead><tr><th>Nombre</th><th>SKU</th><th>Precio</th><th></th></tr></thead>
        <tbody>
        {data?.items.map((p:any) => (
        <tr key={p.id}>
        <td>{p.name}</td>
        <td>{p.sku}</td>
        <td>${p.price}</td>
        <td><Link to={`/products/${p.id}`}>Ver</Link></td>
        </tr>
        ))}
        </tbody>
        </table>


        <div className="pager">
        <button disabled={page<=1} onClick={() => setSp({ page: String(page-1) })}>« Anterior</button>
        <span>Página {page}</span>
        <button onClick={() => setSp({ page: String(page+1) })}>Siguiente »</button>
        </div>
        </div>
    )
}