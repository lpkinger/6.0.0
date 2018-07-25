<%@ page contentType="image/jpeg" import="java.awt.*,java.awt.image.*,java.util.*,javax.imageio.*" pageEncoding="UTF-8"%>
<%!Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
	    'X', 'Y', '3', '4', '5', '6', '7', '8', '9' };	
%>
<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);
	int width = 80, height = 20;
	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	Graphics g = image.getGraphics();

	Random random = new Random();
	
	/* g.setColor(new Color(229, 222, 255)); */
	g.setColor(Color.WHITE); 
	g.fillRect(0, 0, width, height);
	/* g.setColor(getRandColor(200, 250));
	g.fillRect(0, 0, width, height);  */
 	g.setFont(new Font("Times New Roman", Font.ITALIC, 16));
 	/*
    g.setFont(font);*/
	 g.setColor(new Color(224, 224, 224));
	 g.drawRect(0, 0, width - 1, height - 1);

	/*g.setColor(getRandColor(160, 200));
	for (int i = 0; i < 155; i++) {
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		int xl = random.nextInt(12);
		int yl = random.nextInt(12);
		g.drawLine(x, y, x + xl, y + yl);
	} */
	//随机产生干扰线
	 /* for (int i=0;i<random.nextInt(1)+1;i++) {   
                int x1 = random.nextInt(80)%15;   
                int y1 = random.nextInt(20);  
                int x2 = (int) (random.nextInt(80)%40+80*0.7);   
                int y2 = random.nextInt(20);   
                g.setColor(Color.BLACK);  
                g.drawLine(x1, y1, x2, y2);  
            }    */
	
	
	String sRand = "";
	for (int i = 0; i < 4; i++) {
		String rand = String.valueOf(codeSequence[random.nextInt(30)]);
		sRand += rand;
		/* g.setColor(new Color(random.nextInt(80), random.nextInt(80), random.nextInt(80))); */
		 g.setColor(new Color(0, 0, 0));
		g.drawString(rand, 15 * i + 10, 16);
	} 
	session.setAttribute("validcode", sRand);
	g.dispose();

	ServletOutputStream sos = response.getOutputStream();
	ImageIO.write(image, "jpeg", sos);
	sos.flush();
	sos.close();
	sos = null;
	response.flushBuffer();
	out.clear();
	out = pageContext.pushBody();
%>