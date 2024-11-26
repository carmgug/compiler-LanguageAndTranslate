# Project Overview: Programming Language  

This project focuses on developing a programming language with a complete pipeline for source code analysis and execution. 
It includes components for lexical, syntactic, and semantic analysis, along with a code generator that produces executable bytecode from the AST, provided no semantic errors are found.  

## **Main Components**

### 1. Lexer  
- **Description**:  
  The **Lexer** processes the source file (`filecode.text`) and splits the code into **tokens**, which are the fundamental units of the language (keywords, identifiers, symbols, numbers, etc.).  
- **Input**: The source file of the language.  
- **Output**: A sequence of tokens for the parser to analyze.  

### 2. Parser  
- **Description**:  
  The **Parser** takes tokens from the lexer and constructs an **Abstract Syntax Tree (AST)**, a structured representation of the source code.  
- **Key Features**:  
  - Recognizes the grammar of the language.  
  - Reports syntax errors.  
- **Output**: An AST used in the subsequent phases.  

### 3. Semantic  
- **Description**:  
  The **Semantic** module ensures the code is semantically correct by validating rules such as:  
  - Consistent data types in expressions.  
  - Proper declaration and use of variables and functions.  
  - Logical rules specific to the language.  
- **Key Features**:  
  - Reports semantic errors.  
  - Validates the code for bytecode generation.  
- **Output**: A validation signal or a list of errors to be fixed.

### 4. CodeGenerator  
- **Description**:  
  The **CodeGenerator** is responsible for translating the **Abstract Syntax Tree (AST)** into executable bytecode. It operates only if no semantic errors are detected.  
- **Key Features**:  
  - Generates bytecode for language constructs like variables, loops, conditions, and functions.  
  - Performs optimizations to enhance the efficiency of the generated code.  
- **Output**: A bytecode file ready to run on a JVM. 


---

## **Project Pipeline**  
1. The source code is inputted into the **Lexer**, which generates tokens.  
2. Tokens are processed by the **Parser** to create the **AST**.  
3. The **Semantic** module checks the AST for correctness.  
4. The **CodeGenerator** produces bytecode if the code passes the semantic validation.  

---

## **Project Objectives**  
- Provide a complete and modular pipeline for processing source code.  
- Ensure robustness and flexibility with strict syntactic and semantic checks.  
- Produce optimized bytecode for efficient execution.  
