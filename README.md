# Stock Price History — BST Implementation

Projekt akademik për kursin **Data Structures & Algorithms** (Spring 2025).

## Përshkrimi

Aplikacion Java me ndërfaqe në terminal që menaxhon historikun e çmimeve të aksioneve duke përdorur një **Binary Search Tree (BST)**. Çelësat e pemës janë data në formatin `YYYY-MM-DD` — duke qenë se renditja leksikografike përputhet me renditjen kronologjike, BST-ja ofron automatikisht listim të renditur sipas datës.

## Struktura e të dhënave

| Struktura | Përdorimi |
|-----------|-----------|
| `StockBST` | BST kryesor — insert, search, delete, traversal |
| `BSTNode` | Nyja e pemës |
| `StockEntry` | Modeli i të dhënave (datë + çmim mbyllës) |
| `DataLoader` | Lexon/shkruan CSV dhe gjeneron të dhëna demo |

## Funksionalitetet

- **Ngarkim CSV** — lexon të dhëna reale (p.sh. `AAPL_2023_2024.csv`)
- **Të dhëna të gjeneruara** — krijon të dhëna demo për testim
- **CRUD** — shto, kërko, përditëso, fshij rekorde
- **Range query** — listo të gjitha çmimet ndërmjet dy datave
- **Min / Max** — gjej çmimin më të ulët ose më të lartë brenda një periudhe
- **Autosave** — çdo ndryshim ruhet automatikisht në CSV

## Si ta ekzekutosh

```bash
# Kompilo
javac -d out src/dsa/project/tree/*.java

# Ekzekuto
java -cp out dsa.project.tree.Main
```

Pastaj, nga menuja kryesore zgjidh opsionin `1` për të ngarkuar skedarin CSV:
```
AAPL_2023_2024.csv
```

## Struktura e projektit

```
StockPriceHistory/
├── src/dsa/project/tree/
│   ├── Main.java          # Menuja dhe logjika e ndërfaqes
│   ├── StockBST.java      # Implementimi i BST
│   ├── BSTNode.java       # Nyja e pemës
│   ├── StockEntry.java    # Modeli i të dhënave
│   └── DataLoader.java    # I/O për CSV
└── AAPL_2023_2024.csv     # Të dhëna reale Apple 2023–2024
```

## Teknologjitë

- **Gjuha:** Java
- **Struktura kryesore:** Binary Search Tree
- **I/O:** CSV (lexim dhe shkrim)
- **Ndërfaqja:** CLI (Command-Line Interface)
