import http from './http'

export function fetchRecommendedPortfolios() {
  return http.get('/api/portfolio/recommended')
}