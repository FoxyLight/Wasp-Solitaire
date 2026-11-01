"""Design-document authoring only. Does not run or change the Android application."""
from pathlib import Path
from html import escape
import json, base64

ROOT = Path(__file__).parent
OUT = ROOT / 'mockups'
OUT.mkdir(exist_ok=True)
DEAL = json.loads((ROOT / 'deal-seed-123.json').read_text(encoding='utf-8-sig'))
COLS = DEAL['tableau']
SUITS = {'CLUBS':'♣', 'DIAMONDS':'♦', 'HEARTS':'♥', 'SPADES':'♠'}
RANKS = dict(zip(['ACE','TWO','THREE','FOUR','FIVE','SIX','SEVEN','EIGHT','NINE','TEN','JACK','QUEEN','KING'], ['A','2','3','4','5','6','7','8','9','10','J','Q','K']))
GREEN='#165443'; DARK='#103C31'; IVORY='#F7F4EB'; INK='#192327'; RED='#B4232C'; BLUE='#263B62'; GOLD='#FFD782'; MUTED='#D4E1D6'

def rect(x,y,w,h,fill,stroke='none',sw=1,r=0,extra=''):
    return f'<rect x="{x:g}" y="{y:g}" width="{w:g}" height="{h:g}" rx="{r:g}" fill="{fill}" stroke="{stroke}" stroke-width="{sw}" {extra}/>'
def text(x,y,value,size=14,color=IVORY,weight=400,anchor='start',serif=False,extra=''):
    family='Georgia,serif' if serif else 'Arial,sans-serif'
    return f'<text x="{x:g}" y="{y:g}" fill="{color}" font-size="{size:g}" font-weight="{weight}" font-family="{family}" text-anchor="{anchor}" {extra}>{escape(str(value))}</text>'
def line(x1,y1,x2,y2,color,sw=1,extra=''):
    return f'<line x1="{x1:g}" y1="{y1:g}" x2="{x2:g}" y2="{y2:g}" stroke="{color}" stroke-width="{sw}" {extra}/>'
def label(c): return RANKS[c['rank']]+SUITS[c['suit']]
def face(c,x,y,w,h,scale=1,selected=False,hint=False,large=False,extra=''):
    result=[f'<g {extra}>']
    if selected:
        result.append(rect(x-2,y-2,w+4,h+4,GOLD,DARK,1,6))
    result.append(rect(x,y,w,h,IVORY if c['faceUp'] else BLUE,'#637C72' if not selected else DARK,1.2 if not selected else 2,4))
    if not c['faceUp']:
        result.append(rect(x+4,y+4,w-8,h-8,'url(#backweave)',MUTED,.6,2))
    else:
        col=RED if c['suit'] in ['HEARTS','DIAMONDS'] else INK
        fs=(14 if large else 15)*scale
        result.append(text(x+4,y+fs+3,RANKS[c['rank']],fs,col,700,serif=True))
        if large: result.append(text(x+5,y+fs+29,SUITS[c['suit']],28,col,700,serif=True))
        else: result.append(text(x+w-4,y+fs+3,SUITS[c['suit']],fs,col,700,anchor='end',serif=True))
        if not large: result.append(text(x+w*.67,y+h-9,SUITS[c['suit']],w*.42,col,400,anchor='middle',serif=True))
        if selected: result.append(rect(x+2,y+2,3,h-4,DARK,r=1))
    result.append('</g>')
    return ''.join(result)
def button(x,y,w,h,value,primary=False,disabled=False,scale=1,onLight=False):
    fill=(GREEN if onLight else IVORY) if primary else BLUE
    result=rect(x,y,w,h,fill, '#8CA89D' if not primary else fill,1,9)
    result+=text(x+w/2,y+h/2+5*scale,value,14*scale,(IVORY if onLight else INK) if primary else (MUTED if disabled else IVORY),500,'middle')
    return result
def status(w,top=24):
    return text(12,top-8,'9:41',12)+text(w-12,top-8,'Wi-Fi   87%',11,anchor='end')
def chrome(w,top=24,scale=1):
    return rect(0,top,w,48,DARK)+text(12,top+31,'Classic Wasp',20*min(scale,1.3),IVORY,500,serif=True)+text(w-26,top+29,'⋯',24,anchor='middle')
def metadata(w,y,scale=1):
    result=''
    for i in range(3): result+=rect(12+i*6,y+6,13,19,BLUE,IVORY,.7,2)
    result+=text(50,y+21,'3 reserve',12*scale)
    result+=text(w-12,y+21,'♣  ♦  ♥  ♠   0/4',12*scale,IVORY,400,'end')
    return result
def grid(w,y,selected=False,scale=1,large=False,tablet=False,boardw=None,height=500):
    bw=boardw or w
    margin=8 if not tablet else 12
    gutter=4 if not tablet else 6
    cw=(bw-2*margin-6*gutter)/7
    ch=max(cw*1.44,74 if large else 0)
    step=66 if large else (30 if scale>1 else (30 if tablet else 26))
    result=[]
    for ci,cards in enumerate(COLS):
        x=margin+ci*(cw+gutter)
        target=selected and ci==6
        result.append(text(x+cw/2,y+16,('↓ ' if target else '')+str(ci+1),12*min(scale,1.6),GOLD if target else MUTED,500,'middle'))
        if target: result.append(rect(x-2,y+22,cw+4,min(height-24,420),'none',IVORY,2,6,extra='stroke-dasharray="5 4"'))
        cy=y+28
        for idx,c in enumerate(cards):
            chosen=selected and ci==3 and idx>=4
            result.append(face(c,x,cy,cw,ch,scale,chosen,large=large,
                extra=f'data-column="{ci}" data-index="{idx}" data-base-y="{cy}"'))
            cy+=step if c['faceUp'] else 8
    return ''.join(result)
def dock(w,h,bottom=24,selected=False,scale=1):
    row=56 if scale<1.8 else 64
    inspect=48 if scale<1.8 else 56
    if selected: inspect=96
    y=h-bottom-row-inspect
    result=rect(0,y,w,row+inspect+bottom,DARK)
    if selected:
        result+=text(12,y+29,'8♦ + 2 cards selected',14*min(scale,1.3),GOLD,500)
        result+=text(w-12,y+29,'Cancel',14*min(scale,1.3),IVORY,500,'end')
        result+=button(8,y+48,100,48,'Inspect',scale=1.1)
        result+=button(116,y+48,w-124,48,'Choose destination',scale=1.1)
    else:
        result+=text(12,y+inspect/2+5,'Inspect columns',14*min(scale,2),IVORY,500)
        result+=text(w-18,y+inspect/2+5,'⌃',18,IVORY,400,'end')
    by=y+inspect
    for i,name in enumerate(['Undo','Hint','Deal 3']):
        x=8+i*(w-16)/3
        result+=button(x,by+4,(w-16)/3-4,row-8,name,primary=i==2,disabled=i==0,scale=scale)
    result+=rect(w/2-45,h-10,90,4,MUTED,r=2)
    return result
def phone(selected=False,w=376,h=835,scale=1.3,large=False):
    top=41 if w==376 else 24; bottom=48 if w==376 else 24
    meta=72 if large else 32
    result=rect(0,0,w,h,GREEN)+status(w,top)
    if large:
        result+=rect(0,top,w,56,DARK)+text(12,top+38,'Wasp',32,IVORY,500,serif=True)+text(w-24,top+35,'⋯',26,anchor='middle')
        result+=text(12,top+84,'Reserve: 3',24)+text(12,top+114,'0 of 4 suits complete',24)
        gy=top+56+meta
    else:
        result+=chrome(w,top,scale)+metadata(w,top+48,scale)
        gy=top+80
    end=h-bottom-(120 if large else (152 if selected else 104))
    result+=f'<g clip-path="url(#boardclip)">{grid(w,gy,selected,scale,large,height=end-gy)}</g>'
    if large: result+=rect(w-3,gy+28,2,end-gy-32,MUTED,r=1)+rect(w-3,gy+28,2,80,IVORY,r=1)
    result+=dock(w,h,bottom,selected,scale)
    defs=f'<clipPath id="boardclip"><rect x="0" y="{gy}" width="{w}" height="{end-gy}"/></clipPath>'
    return result,defs
def inspection(w=376,h=835,chooser=False):
    top=41; bottom=48; result=rect(0,0,w,h,IVORY)+text(12,top-8,'9:41',12,INK)+text(w-12,top-8,'Wi-Fi   87%',11,INK,anchor='end')
    result+=rect(0,top,w,56,IVORY)+text(20,top+35,'‹',30,INK)+text(56,top+35,'Choose destination' if chooser else 'Column 4',21,INK,500)
    result+=line(0,top+56,w,top+56,'#C5CEC7')
    if chooser:
        result+=text(16,top+92,'Move this complete group',16,INK,500)
        for i,c in enumerate(COLS[3][4:]): result+=face(c,16+i*58,top+110,50,72)
        result+=text(16,top+210,'8♦ leads · 3 cards · order retained',14,INK)
        result+=text(16,top+250,'LEGAL DESTINATION',12,'#475B51',500)
        result+=rect(12,top+270,w-24,96,'#E6EEE8',GREEN,2,10)
        result+=text(28,top+300,'Column 7',19,INK,500)
        result+=text(28,top+328,'Onto 9 of diamonds · 7 cards',15,INK)
        result+=text(w-30,top+328,'→',24,GREEN,500,'end')
        result+=text(16,top+407,'Review the destination, then move.',15,INK)
        result+=text(16,top+435,'Back keeps your selected group.',15,'#475B51')
        result+=button(12,h-bottom-68,w-24,52,'Move to Column 7',True,onLight=True)
        result+=text(w/2,h-bottom-82,'No move has been made yet',13,'#475B51',400,'middle')
    else:
        result+=text(16,top+86,'3 face down · 4 face up',15,'#475B51')
        result+=rect(12,top+104,w-24,52,'#E6EEE8',r=8)
        result+=text(26,top+136,'▧  3 hidden cards — not selectable',14,INK)
        for n,c in enumerate(COLS[3][3:]):
            y=top+168+n*100
            chosen=n>=1
            result+=rect(12,y,w-24,92,'#EDF2EC' if chosen else IVORY,GREEN if chosen else '#C5CEC7',2 if chosen else 1,9)
            result+=face(c,24,y+8,52,75,1.15,selected=chosen)
            name=({'JACK':'Jack','EIGHT':'Eight','FIVE':'Five','TWO':'Two'}[c['rank']]+' of '+c['suit'].lower())
            result+=text(92,y+32,name,17,INK,500)
            result+=text(92,y+60,'Selected start · 3-card tail' if n==1 else ('Attached to selected tail' if n>1 else 'Select this card + all below'),13,GREEN if chosen else '#475B51')
            if n==1: result+=text(w-24,y+33,'✓',19,GREEN,500,'end')
        result+=line(0,h-bottom-100,w,h-bottom-100,'#C5CEC7')
        result+=text(16,h-bottom-77,'Selected: 8♦ + 2 attached cards',14,INK,500)
        result+=button(12,h-bottom-64,92,52,'Cancel')
        result+=button(112,h-bottom-64,w-124,52,'Choose destination',True,onLight=True)
    result+=rect(w/2-45,h-18,90,4,INK,r=2)
    return result,''
def landscape():
    w,h=700,360; top,bottom=24,24; bw=w-80
    result=rect(0,0,w,h,GREEN)+status(w,top)+chrome(bw,top,1)+metadata(bw,top+48)
    result+=f'<g clip-path="url(#landclip)">{grid(bw,top+80,height=208)}</g>'
    result+=rect(bw-3,top+108,2,h-top-bottom-112,MUTED,r=1)+rect(bw-3,top+108,2,70,IVORY,r=1)
    result+=rect(w-80,top,80,h-top-bottom,DARK)
    for i,label_ in enumerate(['Undo','Hint','Deal 3','Inspect']): result+=button(w-72,top+12+i*60,64,50,label_,primary=i==2,disabled=i==0)
    result+=rect(w/2-45,h-10,90,4,MUTED,r=2)
    return result,f'<clipPath id="landclip"><rect x="0" y="104" width="{bw}" height="{h-104-bottom}"/></clipPath>'
def tablet():
    w,h=1000,760; bw=660
    result=rect(0,0,w,h,GREEN)+status(w)+chrome(bw,24)+metadata(bw,72)
    result+=grid(bw,108,True,scale=1.3,tablet=True,height=500)
    result+=rect(0,h-100,bw,100,DARK)
    result+=text(16,h-74,'8♦ + 2 attached · destination: Column 7',16,GOLD,500)
    for i,n in enumerate(['Undo','Hint','Deal 3']): result+=button(16+i*150,h-60,140,48,n,primary=i==2,disabled=i==0)
    result+=text(bw-18,h-23,'Cancel',15,anchor='end')
    result+=rect(bw,24,w-bw,h-48,IVORY)
    result+=text(bw+20,61,'Column 4',21,INK,500)+text(w-20,61,'×',22,INK,400,'end')
    result+=text(bw+20,92,'3 hidden · 4 face up',14,'#475B51')
    for n,c in enumerate(COLS[3][3:]):
        y=114+n*104
        result+=rect(bw+12,y,316,94,'#E6EEE8' if n>=1 else IVORY,GREEN if n>=1 else '#C5CEC7',1,8)
        result+=face(c,bw+24,y+8,54,78,selected=n>=1)
        cardname=({'JACK':'Jack','EIGHT':'Eight','FIVE':'Five','TWO':'Two'}[c['rank']]+' of '+c['suit'].lower())
        result+=text(bw+92,y+33,cardname,17,INK,500)
        result+=text(bw+92,y+62,'Selected · 3 cards' if n==1 else ('Attached' if n>1 else 'Select tail'),14,INK)
    result+=button(bw+16,h-100,308,52,'Choose destination',True,onLight=True)
    result+=text(bw+20,h-120,'Same board. Same selected group.',14,INK)
    return result,''
def svg(name,w,h,content,defs=''):
    content=content.replace('url(#',f'url(#{name}-')
    defs=defs.replace('id="',f'id="{name}-')
    return f'''<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}" role="img" aria-label="{escape(name)}" data-design="{escape(name)}">
<title>{escape(name)} — UNAPPROVED Phase 4D design proposal</title>
<desc>Classic Wasp seed 123. Seven columns, 49 tableau cards, 12 hidden cards and three reserve cards. This is a design illustration, not an implemented application.</desc>
<defs><pattern id="{name}-backweave" width="8" height="8" patternUnits="userSpaceOnUse"><rect width="8" height="8" fill="{BLUE}"/><path d="M0 8L8 0" stroke="{MUTED}" stroke-opacity=".18"/></pattern>{defs}</defs>
{content}</svg>'''

SPECS=[('portrait',376,835,lambda:phone()),('selected-tail',376,835,lambda:phone(True)),
       ('column-inspection',376,835,lambda:inspection()),('destination-chooser',376,835,lambda:inspection(chooser=True)),
       ('short-landscape',700,360,landscape),('tablet',1000,760,tablet),
       ('large-text',320,720,lambda:phone(w=320,h=720,scale=2,large=True))]
for name,w,h,fn in SPECS:
    content,defs=fn(); (OUT/f'{name}.svg').write_text(svg(name,w,h,content,defs),encoding='utf-8')

def luminance(hex_):
    vals=[int(hex_[i:i+2],16)/255 for i in (1,3,5)]
    lin=[v/12.92 if v<=.04045 else ((v+.055)/1.055)**2.4 for v in vals]
    return sum(a*b for a,b in zip(lin,[.2126,.7152,.0722]))
rows=[]
for role,fg,bg in [('Card rank, black',INK,IVORY),('Card rank, red',RED,IVORY),('Table text',IVORY,GREEN),('Secondary table text',MUTED,GREEN),('Button text',IVORY,BLUE),('Table accent',GOLD,GREEN),('Selection gold on ivory',GOLD,IVORY),('Selection inner edge',DARK,IVORY)]:
    a,b=sorted([luminance(fg),luminance(bg)])
    rows.append({'role':role,'foreground':fg,'background':bg,'ratio':round((b+.05)/(a+.05),2)})
(ROOT/'contrast-calculations.json').write_text(json.dumps(rows,indent=2)+'\n',encoding='utf-8')
print('Created',len(SPECS),'mockups; contrast pairs:',rows)
