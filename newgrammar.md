# Our New Let Lang Grammar Rules

factor &rarr; **tuple (** [(id | num | factor) {, (id | num | factor)}] **)**

expr &rarr; **let** [(id) {, (id)}] **:=** [(expr) {, (expr)}] **in** expr 

global &rarr; **global** [(id) {, (id)}] **:=** [(expr) {, (expr)}]

comment &rarr; **#** [^#] **#**

fun &rarr; **fun** id **:=** lexpr 

lexpr &rarr; **( lambda** id **=>** (expr | lexpr) **)** 

