# Assumptions
1. A user is authenticated and has an id
2. A user can only ever have 1 mutable cart
3. The cart has shared mutable state and could be accessed by many thread
4. Write operation as frequent, comparable with reads
5. Currency is fixed and not specified
6. Data is available to produce an itemized item but no attempt made at presentation
7. Odd numbers of items > 2 get 2 for 1 if applied in each matching pair, 3 * cornflake cost 2 8 unit price