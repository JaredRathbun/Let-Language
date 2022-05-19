## Let Language Grammar Rules

prog &rarr; fun { fun }

fun &rarr;  **fun** id lexpr | expr

expr &rarr; **let** id **:=** expr **in** expr | rexpr { ( **and** | **or** ) rexpr } | **not** rexpr

 | **if** expr **then** expr **else** expr | **apply** ( id | **(** lexpr **)** ) expr


rexpr &rarr; mexpr [ ( **<** | **>** | **>=** | **<=** | **=** ) mexpr ]

mexpr &rarr; term { ( **+** | **-** ) term }

<!-- exprLst &rarr; **list(** [ expr {, expr} ] **)** | id -->

term &rarr; factor { ( <strong>*</strong> | **/**  | **++** ) factor }

factor &rarr; id | num | **(** expr **)** | **list (** [ (id | num ) {, (id | num)} ]**)**
  | **hd** expr

  | **tl** expr | **true** | **false**

lexpr &rarr; id => expr
