program Project2;


{$APPTYPE CONSOLE}
var N,p,a,o,c,k,s,d:integer;
begin
write('N=');
readln(N);
p:=0;
  for a:=10 to 10000000 do begin
     k:=1;
     o:=a mod 10;
     c:=a div 10;
     d:=a;
     while d>9 do begin
        d:=d div 10;
        k:=k*10;
     end;
     o:=o*k;
     s:=c+o;
     if N*a=s then begin
       writeln(' a=',a,' s=',s);
       p:=p+1;
     end;
  end;   
if p=0 then writeln('These numbers do not exist!');
write('Press Enter to exit');
readln;
end.
