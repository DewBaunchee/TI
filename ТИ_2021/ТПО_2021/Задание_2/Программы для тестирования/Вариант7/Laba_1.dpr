program Laba_1;

{$APPTYPE CONSOLE}
uses
  System.SysUtils;
procedure ReadCheck(var x:integer);
begin
readln(x);
while x<=0 do
begin
writeln ('Dannoe chislo ne naturalnoe, povtorite vvod');
readln(x);
end;
end;
var M,N,i,k,sum,p:integer;
begin
p:=0;
repeat
ReadCheck(M);
ReadCheck(N);
if N<=M then
writeln('Ukazan nevernuy promezhutok,povtorite vvod');
until N>M ;
k:=0;
for i:=M to N do
  begin
  sum:=0;
  for k:=1 to i-1 do
    begin
    if i mod k=0 then sum:=sum+k;
    end;
  if i=sum then writeln(i);
  if i=sum then p:=p+1;
  end;
if p=0 then writeln('Chisel net')
else writeln('Kolichestvo chisel ',p);
  readln;
end.
