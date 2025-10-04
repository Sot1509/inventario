import { describe, it, expect, vi } from 'vitest'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { MemoryRouter } from 'react-router-dom'
import { render, screen } from '@testing-library/react'
import * as api from '../api/products'
import ProductsListPage from './ProductsListPage'

describe('ProductsListPage', () => {
  it('renderiza la lista de productos', async () => {
    vi.spyOn(api, 'listProducts').mockResolvedValue({
      items: [
        { id: '1', sku: 'SKU-1', name: 'Teclado', price: 59.9 },
        { id: '2', sku: 'SKU-2', name: 'Auriculares', price: 149.9 },
      ],
      meta: {}, links: {}
    })

    const qc = new QueryClient()
    render(
      <QueryClientProvider client={qc}>
        <MemoryRouter>
          <ProductsListPage />
        </MemoryRouter>
      </QueryClientProvider>
    )

    expect(await screen.findByText('Teclado')).toBeInTheDocument()
    expect(await screen.findByText('Auriculares')).toBeInTheDocument()
  })
})
