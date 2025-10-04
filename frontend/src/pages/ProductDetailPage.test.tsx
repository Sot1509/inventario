import { describe, it, expect, vi } from 'vitest'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { render, screen, within, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
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
    const user = userEvent.setup()

    render(
      <QueryClientProvider client={qc}>
        <MemoryRouter initialEntries={['/products/1']}>
          <Routes>
            <Route path="/products/:id" element={<ProductDetailPage />} />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>
    )

    
    const qtyLine = await screen.findByText(/Cantidad disponible:/)
    expect(within(qtyLine).getByText('5')).toBeInTheDocument()

    
    await user.click(screen.getByText('Comprar 1'))

    
    await waitFor(() => {
      expect(decSpy).toHaveBeenCalledWith('1', 1)
    })

    
    expect(qtySpy).toHaveBeenCalled()
  })
})
