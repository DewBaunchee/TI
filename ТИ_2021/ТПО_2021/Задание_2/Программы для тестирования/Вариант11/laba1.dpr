program laba1;
{$APPTYPE CONSOLE}

var a,sum,n,m,i,k: longint;

begin
Writeln('A pair of numbers that are multiples 7:');
Writeln('Enter the range(up to):');
Readln (a);
k:=0;
for i:=1 to a do
begin
m:=i; 
sum:=0; 
While m>0 do
begin 
sum:=sum+m mod 10; 
m:=m div 10;
end;
if sum mod 7=0 then 
begin
m:=i+1;
sum:=0;
While m>0 do
begin
sum:=sum+m mod 10;
m:=m div 10;
end;
if sum mod 7=0 then
begin
k:=k+1;
Writeln(i,' ',i+1);
end;
end;
end;
if k=0 then write('k=0')
else write('Such pairs: ',k);
readln
end.
