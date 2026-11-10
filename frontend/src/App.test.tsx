import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { App } from './App'

describe('App', () => {
  it('renders the project foundation welcome', () => {
    render(<App />)

    expect(screen.getByRole('heading', { name: 'CampusConnect' })).toBeInTheDocument()
    expect(screen.getByText('Project foundation is ready.')).toBeInTheDocument()
  })
})
