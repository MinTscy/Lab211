# E-Commerce Simulation Flow (Shopee/Lazada-Style Marketplace)
```mermaid
flowchart TD
    Start([Start]) --> OpenApp[Open App / Website]
    OpenApp --> Login{Logged in?}

    Login -- No --> Register[Register / Login]
    Register --> Browse[Browse Products]

    Login -- Yes --> Browse[Browse Products]

    Browse --> View[View Product Detail]
    View --> AddCart{Add to cart?}

    AddCart -- No --> Browse
    AddCart -- Yes --> Cart[View Cart]

    Cart --> Checkout{Checkout?}
    Checkout -- No --> Browse

    Checkout -- Yes --> Address[Enter Address]
    Address --> Payment[Choose Payment Method]

    Payment --> Pay{Payment Success?}
    Pay -- No --> Payment
    Pay -- Yes --> Order[Order Confirmed]

    Order --> Ship[Seller Ships Order]
    Ship --> Receive[Customer Receives Order]
    Receive --> End([End])


