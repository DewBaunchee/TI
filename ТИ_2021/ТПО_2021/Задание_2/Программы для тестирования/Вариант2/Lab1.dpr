 program Lab1;

{$APPTYPE CONSOLE}

uses
  SysUtils;

var sum,k,q,p,l:integer;
     n,m:real;
 f:boolean;
 begin
 { TODO -oUser -cConsole Main : Insert code here }
 writeln('Vvedite naturalnoe cislo  n ');
 readln(n);
 writeln('Vvedite naturalnoe cislo  m ');
 readln(m);
 f:=false;
 while f=false do begin
 if ((n<=1) or (trunc(n)<>n)) or ((m<=0) or (trunc(m)<>m)) then begin
                                  writeln('Znacenie nekorrektno Vvedite naturalnoe cislo n ');
                                  readln(n);
                                  writeln('Vvedite naturalnoe cislo m');
                                  readln(m);
                                  end
                             else f:=true;
                   end;
l:=0;
k:=1; 
repeat begin
  sum:=0;p:=k;
     while p<>0 do begin
     q:=p mod 10;
     sum:=sum+q;
     p:=p div 10;
     end;
     if (sqr(sum)=m) then  begin
                           l:=l+1;
                           write(k,' ');
                           end;
     k:=k+1;
                 end;
until k=n;
writeln(l);
readln;
end.

 