import qrcode
from qrcode.image.styledpil import StyledPilImage
from qrcode.image.styles.moduledrawers import RoundedModuleDrawer

vcard = """BEGIN:VCARD
VERSION:3.0
N:Guerrero-Ruiz;Alejandro;;;
FN:Alejandro Guerrero-Ruiz
ORG:OECD;Development Co-operation Directorate
TITLE:Head, OECD Development Impact - Reforms and Partnerships for Development Impact Division
ADR;TYPE=WORK:;;2, rue Andre Pascal;Paris;;75775 Cedex 16;France
TEL;TYPE=WORK,VOICE:+33145248363
TEL;TYPE=CELL:+34616367846
EMAIL;TYPE=WORK:Alejandro.GUERRERO-RUIZ@oecd.org
URL:https://www.oecd.org
END:VCARD"""

qr = qrcode.QRCode(
    version=None,
    error_correction=qrcode.constants.ERROR_CORRECT_M,
    box_size=20,
    border=4,
)
qr.add_data(vcard)
qr.make(fit=True)

img = qr.make_image(
    image_factory=StyledPilImage,
    module_drawer=RoundedModuleDrawer(),
    fill_color="#003399",
    back_color="white",
)
img.save("alejandro_guerrero_contact_qr.png")
print("QR code saved as alejandro_guerrero_contact_qr.png")
