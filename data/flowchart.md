# E-Commerce Simulation Flow (Shopee/Lazada-Style Marketplace)
```mermaid
flowchart LR

%% =======================
%% 1) USER BROWSING
%% =======================
subgraph S1[1. User Browsing]
direction TD
A[Marketplace Entry] --> B[Browse Products]
B --> C[Select Variant]
C --> D[Add to Cart]
D --> E{Continue Shopping?}
E -- Yes --> B
E -- No --> F[View Cart]
end

%% =======================
%% 2) DISCOUNTS & VOUCHERS
%% =======================
subgraph S2[2. Discounts & Vouchers]
direction TD
F --> G[Apply Flash Sale Discount]
G --> H{Flash Sale Eligible?}
H --> I[Apply Shop Voucher]

I --> J{Shop Voucher Eligible?}
J --> K[Apply Platform Voucher]

K --> L{Platform Voucher Eligible?}
L --> M[Apply Shipping Promotion]

M --> N{Shipping Promo Eligible?}
N --> O[Compute Final Price]
end

%% =======================
%% 3) CHECKOUT INIT
%% =======================
subgraph S3[3. Checkout Init]
direction TD
O --> P[Initiate Checkout]
P --> Q[Re-Validate Stock & Price]
Q --> R{Price or Stock Changed?}
R -- No --> V[Proceed to Order Submission]
R -- Yes --> S[Ask User to Confirm Update]
S --> T{User Confirms?}
T -- No --> U[Abort Checkout]
T -- Yes --> V
end

%% =======================
%% 4) TRANSACTION & LOCKING
%% =======================
subgraph S4[4. Transaction & Locking]
direction TD
V --> W[BEGIN TRANSACTION]
W --> X{Idempotency Used?}
X -- Yes --> Y[Return Existing Order Result]
X -- No --> Z[Lock Inventory Row]
end

%% =======================
%% 5) VALIDATION & APPLY
%% =======================
subgraph S5[5. Validation & Apply]
direction TD
Z --> AA{Stock Available?}
AA -- No --> AB[SOLD OUT]

AA -- Yes --> AC{Purchase Limit Reached?}
AC -- Yes --> AD[PURCHASE LIMIT REACHED]

AC -- No --> AE{Voucher Valid?}
AE -- No --> AF[VOUCHER INVALID]

AE -- Yes --> AG{Voucher Quota Available?}
AG -- No --> AF

AG -- Yes --> AH[Decrement Voucher Quota]
AH --> AI[Update Inventory]
end

%% =======================
%% 6) COMMIT
%% =======================
subgraph S6[6. Commit]
direction TD
AI --> AJ[Create Order Record]
AJ --> AK[COMMIT TRANSACTION]
AK --> AL[Order Confirmation]
end

%% =======================
%% 7) FAILURES (ROLLBACK)
%% =======================
subgraph S7[7. Failures]
direction TD
AB --> AM[ROLLBACK TRANSACTION]
AD --> AM
AF --> AM
AM --> AN[Order Failure]
end

%% =======================
%% 8) PRICE CHANGE FAIL (API RETURN)
%% =======================
Q -.-> AO[Return PRICE CHANGED]

%% =======================
%% Styling (optional)
%% =======================
classDef ok fill:#eaffea,stroke:#2e7d32,stroke-width:1px;
classDef fail fill:#ffecec,stroke:#c62828,stroke-width:1px;
classDef step fill:#f7f7ff,stroke:#4a4a9e,stroke-width:1px;
classDef decision fill:#fff8e1,stroke:#b26a00,stroke-width:1px;

class AL,Y ok;
class AB,AD,AF,AM,AN,AO,U fail;
class A,B,C,D,F,G,I,K,M,O,P,Q,V,W,Z,AH,AI,AJ,AK step;
class E,H,J,L,N,R,T,X,AA,AC,AE,AG decision;