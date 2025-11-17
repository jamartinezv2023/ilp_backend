resource "azurerm_resource_group" "ilp_rg" {
  name     = var.resource_group_name
  location = var.location
}

resource "azurerm_container_registry" "ilp_acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.ilp_rg.name
  location            = azurerm_resource_group.ilp_rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

resource "azurerm_kubernetes_cluster" "ilp_aks" {
  name                = var.aks_cluster_name
  location            = azurerm_resource_group.ilp_rg.location
  resource_group_name = azurerm_resource_group.ilp_rg.name
  dns_prefix          = "ilp-aks"

  default_node_pool {
    name       = "system"
    node_count = 2
    vm_size    = "Standard_DS2_v2"
  }

  identity {
    type = "SystemAssigned"
  }

  network_profile {
    network_plugin    = "azure"
    load_balancer_sku = "standard"
  }
}

resource "azurerm_postgresql_flexible_server" "ilp_pg" {
  name                = var.postgres_server_name
  resource_group_name = azurerm_resource_group.ilp_rg.name
  location            = azurerm_resource_group.ilp_rg.location

  administrator_login          = var.postgres_admin_user
  administrator_login_password = var.postgres_admin_password

  sku_name   = "B_Standard_B1ms"
  storage_mb = 32768
  version    = "16"

  backup {
    backup_retention_days = 7
  }

  high_availability {
    mode = "Disabled"
  }

  maintenance_window {
    day_of_week  = 0
    start_hour   = 0
    start_minute = 0
  }

  authentication {
    password_auth_enabled = true
  }
}

resource "azurerm_postgresql_flexible_server_database" "ilp_pg_db" {
  name      = "bd_colegios"
  server_id = azurerm_postgresql_flexible_server.ilp_pg.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}
