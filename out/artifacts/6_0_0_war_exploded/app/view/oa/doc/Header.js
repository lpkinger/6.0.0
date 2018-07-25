Ext.define('erp.view.oa.doc.Header', {
	extend: 'Ext.panel.Panel', 
	alias: 'widget.erpHeader',
	//extend: 'Ext.container.Viewport',
	//extend: 'Ext.Toolbar',
	//xtype : 'pageHeader',
	//ui   : 'sencha',
	region: 'north',
	cls:'northpanel',
	layout: 'hbox',
	align: 'middle',
	width:'100%',
	height: 50,
	style: {border: 'none'},
	hideBorders: true,
	items: [{
				xtype: 'image',
				cls:'logo',
				src:'../../../jsps/oa/doc/resources/images/images/logored.png'
			},{
				xtype: 'tbtext',
				html: '<img src="../../../jsps/oa/doc/resources/images/images/person.png">',
				width: 30,
				cls:'header-user'
			},{
			    xtype:'tbtext',
			    cls:'user_info',
			    html:ma_name+'>'+em_name
	 		},{
				xtype: 'tbtext',
				flex: 1,
				text: ''
			},{
				xtype: 'button',
				text: '首页',
				cls:'headbtn',
				id:'docIndex',
				iconCls:'home'
			},{
				xtype: 'tbtext',
				align: 'end',
				text: '|',
				cls:'header-split'
			},{
				xtype: 'button',
				text: '联系我们',
				cls:'headbtn',
				id:'aboutus',
				iconCls:'about',
				listeners:{
					mouseover:function(){
						var buttons=Ext.getCmp("aboutus");
						buttons.showMenu();	
					}
				},
				menu:{
					items:[{	
						margin: '0.5 0 0.5 0',
						readOnly : true,
						xtype:'textfield',
						value:'电话:400-830-1818'
					},{	
						margin: '0.5 0 0.5 0',
						readOnly : true,
						xtype:'textfield',
						value:'邮箱:info@usoftchina.com',
						href:'info@usoftchina.com'
					}]
				}
			},{
				xtype: 'tbtext',
				align: 'end',
				text: '|',
				cls:'header-split'
			},{
				xtype: 'button',
				text: '退出',
				id:'docExit',
				cls:'headbtn',
				iconCls:'quit'
			}
			/*,{
				xtype: 'component',
				cls  : 'x-logo',
				html : 'UAS文档管理',
				hideBorders: true,
			},{
			   xtype:'tbtext',
			   cls:'topMenu-2',
			   style:'padding: 25px 10px 0px 100px;font-size:15px;font-weight: 600;',
			   text:'欢迎 ，'+em_name+"!"
			 },{
				xtype:'tbtext',
				style:'padding: 25px 0px 0px 100px;font-size:15px;font-weight: 800;',
				text:'当前位置:'
			 },{
				xtype:'tbtext',
				style:'padding:25px 10px 0px 5px;font-size:15px;font-weight:600;',
				id:'virtualpath'
			 },{
				xtype:'tbtext',
				style:'padding:25px 10px 0px 5px;font-size:15px;font-weight:600;',
				id:'virtualpath'
			 },'->',{
				xtype:'container',
				style:'padding:25px 10px 0px 500px;font-size:15px;font-weight:600;',
				html:'<div id="searchBox"><form id="searchDoc" name="searchDoc" onsubmit="search(); return false" action="/search/search.html" method="post"><input type="text" name="keywords" value="" id="keywords" class="inputTextNormal"> <input id="searchBtn" type="image" src="../doc/resources/images/btn-search.jpg" value="搜索文档"></form> </div>'
			 }*/
		]
});
