/**
 * Único lugar que conhece os caminhos da API.
 *
 * Os caminhos são relativos (sem host): em produção o Nginx serve o front e faz
 * proxy de /api e /auth para o Spring Boot; em desenvolvimento quem faz isso é
 * o proxy do `ng serve` (proxy.conf.json). Assim o mesmo build funciona em
 * qualquer ambiente, sem endereço fixo no código.
 */
export const API = {
  auth: '/auth',
  reunioes: '/api/reunioes',
  acoes: '/api/reunioes/acoes',
  colaboradores: '/api/reunioes/colaboradores',
  dashboard: '/api/dashboard',
  feriados: '/api/reunioes/feriados',
} as const;
