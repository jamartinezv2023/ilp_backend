output "resource_group_name" {
  value = azurerm_resource_group.ilp_rg.name
}

output "aks_cluster_name" {
  value = azurerm_kubernetes_cluster.ilp_aks.name
}

output "acr_login_server" {
  value = azurerm_container_registry.ilp_acr.login_server
}

output "postgres_fqdn" {
  value = azurerm_postgresql_flexible_server.ilp_pg.fqdn
}
