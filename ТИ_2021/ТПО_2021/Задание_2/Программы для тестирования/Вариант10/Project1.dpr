program lab_1;

{$APPTYPE CONSOLE}

uses
  SysUtils;

var p,a,n,i:integer;
  begin

  writeln('Enter a natural number:');
  readln(n);
  writeln('Found following numbers:');
  for i:=1 to n do
  begin
   p:=1;
   a:=i;
    while(a>0) do
    begin
      a:=a div 10;
      p:=p*10;
    end;
    if (sqr(i)-i) mod p=0 then writeln(i,' ',i*i);
  end;
  readln;
end.
