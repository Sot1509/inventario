import { describe, it, expect, vi } from 'vitest'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { fireEvent, render, screen } from '@testing-library/react'
import * as products from '../api/products'
import * as inventory from '../api/inventory'
import ProductDetailPage from './ProductDetailPage'

describe('ProductDetailPage', () => {
  it('muestra qty y permite comprar', async () => {
    vi.spyOn(products, 'getProduct').mockResolvedValue({
      id: '1', sku: 'SKU-1', name: 'Teclado', description: 'Mecánico', price: 59.9
    } as any)
    const qtySpy = vi.spyOn(inventory, 'getQuantity').mockResolvedValue(5)
    const decSpy = vi.spyOn(inventory, 'decrement').mockResolvedValue(4)

    const qc = new QueryClient()
    render(
      <QueryClientProvider client={qc}>
        <MemoryRouter initialEntries={['/products/1']}>
          <Routes>
            <Route path="/products/:id" element={<ProductDetailPage />} />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>
    )

    // carga inicial
    expect(await screen.findByText(/Cantidad disponible:/)).toBeInTheDocument()
    expect(screen.getByText(/5/)).toBeInTheDocument()

    // comprar 1
    fireEvent.click(screen.getByText('Comprar 1'))

    // se refresca qty (invalidateQueries) y se llama decrement
    expect(decSpy).toHaveBeenCalledWith('1', 1)
    expect(qtySpy).toHaveBeenCalled()
  })
})
