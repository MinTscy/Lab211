# E-Commerce Simulation Flow (Shopee/Lazada-Style Marketplace)
```mermaid
flowchart TD

%% =========================
%% LANE 1 — BROWSING & DISCOUNTS
%% =========================
subgraph L1["Lane 1 — Browsing & Discounts"]
  direction LR
  A[Marketplace Entry] --> B[Product Browsing] --> C[Select Product Variant]
  C --> D[Add to Cart] --> E{Continue Shopping?}
  E -- Yes --> B
  E -- No --> F[View Cart]

  F --> G[Apply Flash Sale Discount] --> H{Flash Sale Eligible?}
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
end

%% =========================
%% LANE 2 — CHECKOUT & TRANSACTION
%% =========================
subgraph L2["Lane 2 — Checkout & Transaction"]
  direction LR
  O --> P[Initiate Checkout] --> Q[Re-Validate Stock & Price]
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
end

%% =========================
%% LANE 3 — VALIDATION & COMMIT
%% =========================
subgraph L3["Lane 3 — Validation, Commit & Failures"]
  direction LR
  Z --> AA{Stock Available?}
  AA -- No --> AB[Return SOLD OUT]

  AA -- Yes --> AC{Purchase Limit Reached?}
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
end

%% Optional API return
Q -.-> AO[Return PRICE CHANGED]
