@startuml
skinparam classAttributeIconSize 0
hide empty members

'======================
' CLASSES (Simplified)
'======================
class User {
  +user_id : int <<PK>>
  +username : String
  +email : String
  +password_hash : String
  +role : Role
  --
  +login() : boolean
  +logout() : void
  +updateProfile() : void
}

enum Role {
  buyer
  seller
  admin
}

class Shop {
  +shop_id : int <<PK>>
  +shop_name : String
  +owner_id : int <<FK>>
  --
  +createShop() : void
  +updateShop() : void
}

class Product {
  +product_id : int <<PK>>
  +shop_id : int <<FK>>
  +product_name : String
  +base_price : decimal
  --
  +createProduct() : void
  +updatePrice(newPrice: decimal) : void
}

class ProductVariant {
  +variant_id : int <<PK>>
  +product_id : int <<FK>>
  +size : String
  +color : String
  +price : decimal
  +stock_quantity : int
  --
  +updateStock(delta: int) : void
}

class Order {
  +order_id : int <<PK>>
  +user_id : int <<FK>>
  +shop_id : int <<FK>>
  +status : OrderStatus
  +total_amount : decimal
  +order_date : datetime
  --
  +createOrder() : void
  +updateStatus(newStatus: OrderStatus) : void
  +cancelOrder() : void
}

enum OrderStatus {
  CREATED
  PAID
  PACKING
  SHIPPING
  COMPLETED
  CANCELLED
}

class OrderItem {
  +order_item_id : int <<PK>>
  +order_id : int <<FK>>
  +product_id : int <<FK>>
  +variant_id : int <<FK>>
  +quantity : int
  +unit_price : decimal
  --
  +calculateTotal() : decimal
}

class Voucher {
  +voucher_id : int <<PK>>
  +voucher_code : String
  +discount_value : int
  +total_quota : int
  +used_quota : int
  --
  +isValid(now: datetime) : boolean
  +applyVoucher(total: decimal) : decimal
}

'======================
' RELATIONSHIPS (FK)
'======================
User "1" <-- "0..*" Shop : owner_id
Shop "1" <-- "0..*" Product : shop_id
Product "1" <-- "0..*" ProductVariant : product_id

User "1" <-- "0..*" Order : user_id
Shop "1" <-- "0..*" Order : shop_id
Order "1" <-- "1..*" OrderItem : order_id
Product "1" <-- "0..*" OrderItem : product_id
ProductVariant "0..1" <-- "0..*" OrderItem : variant_id
@enduml