program NOD;

{$APPTYPE CONSOLE}

uses
  SysUtils;
var
  n,x,y:string;
  f,c,u,a,b,g,d,k:integer;
begin
  repeat
    f:=0;
    writeln('Please, type a number of numbers');
    readln(n);
    val(n,c,u);
    if u<>0 then f:=1;
    if c<2 then f:=1;
  until f=0;
    repeat
    f:=0;
    writeln('Please, type a number');
    readln(x);
    val(x,g,u);
    if u<>0 then f:=1;
    if g<1 then f:=1
    until f=0;
      k:=1;
      repeat
      k:=k+1;
        repeat
        f:=0;
        writeln('Please, type a number N',k);
        readln(y);
        val(y,d,u);
        if u<>0 then f:=1;
        if d<1 then f:=1
        until f=0;
      a:=g;
      b:=d;
        repeat;
        if a>b then a:=a-b;
        if a<b then b:=b-a;
        until a=b;
      g:=(g div a)*d;
      until k=c;
  writeln('NOK=',g);
  readln;
End.



