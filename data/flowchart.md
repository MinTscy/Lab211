# E-Commerce Simulation Flow (Shopee/Lazada-Style Marketplace)
```mermaid
flowchart LR


  A[Marketplace Entry] --> B[Product Browsing]
  B --> C[Select Product Variant]
  C --> D[Add to Cart]
  D --> E{Continue Shopping?}
  E -- Yes --> B
  E -- No --> F[View Cart]

  F --> G[Apply Flash Sale Discount]
  G --> H{Flash Sale Eligible?}
  H -- Yes --> I[Apply Shop Voucher]
  H -- No --> I

  I --> J{Shop Voucher Eligible?}
  J -- Yes --> K[Apply Platform Voucher]
  J -- No --> K

  K --> L{Platform Voucher Eligible?}
  L -- Yes --> M[Apply Shipping Promotion]
  L -- No --> M

  M --> N{Shipping Promotion Eligible?}
  N -- Yes --> O[Compute Final Price]
  N -- No --> O

  O --> P[Initiate Checkout]
  P --> Q[Re-Validate Stock & Price]

  Q --> R{Price or Stock Changed?}
  R -- Yes --> S[Ask User to Confirm Update]
  S --> T{User Confirms?}
  T -- No --> U[Abort Checkout]
  T -- Yes --> V[Proceed to Order Submission]
  R -- No --> V

  V --> W[BEGIN TRANSACTION]
  W --> X{Idempotency Used?}
  X -- Yes --> Y[Return Existing Order Result]
  X -- No --> Z[Lock Inventory Row]

  Z --> AA{Stock Available?}
  AA -- No --> AB[Return SOLD OUT]
  AA -- Yes --> AC{Per-User Purchase Limit Reached?}
  AC -- Yes --> AD[Return PURCHASE LIMIT REACHED]
  AC -- No --> AE{Voucher Valid?}
  AE -- No --> AF[Return VOUCHER INVALID]
  AE -- Yes --> AG{Voucher Quota Available?}
  AG -- No --> AF
  AG -- Yes --> AH[Decrement Voucher Quota]
  AH --> AI[Update Inventory]

  AI --> AJ[Create Order Record]
  AJ --> AK[COMMIT TRANSACTION]
  AK --> AL[Order Confirmation]

  AB --> AM[ROLLBACK TRANSACTION]
  AD --> AM
  AF --> AM
  AM --> AN[Order Failure]

  Q -.-> AO[Return PRICE CHANGED]
