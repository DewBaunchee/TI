program LabRab1;

{$APPTYPE CONSOLE}

var
    i,k,x,n,y:integer;
    a:array[1..1] of integer;
begin
write('Vvedite kolichestvo chisel: ');
readln(n);
repeat
   for i:=1 to n do
   readln(a[i]);
   if a[1]=0 then write('Postavte na pervoe mesto chislo, ne ravnoe 0, esli vse chisla ravni 0, to NOD=beskonechnost');
   k:=1;
   x:=1;
   while k <> abs(a[1]) do
     begin
     k:=k+1;
     y:=0;
     for i:=1 to n do
     if a[i] mod k=0 then y:=y+1;
     if y=n then x:=k;
     end;
   writeln('NOD=',x);
   readln;
until n<2;
end.
