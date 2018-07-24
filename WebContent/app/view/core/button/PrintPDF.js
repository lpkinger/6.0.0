/**
 * 打印
 * 调用浏览器打印方法
 */	
Ext.define('erp.view.core.button.PrintPDF',{ 
		id:'printpdf',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintPDFButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.addEvents({
				'beforePrint': true
			});
			this.callParent(arguments); 
		},
		listeners: {
			click:function(btn){
				var me=this;
				me.fireEvent('beforePrint');//可在此事件中隐藏不想打印的字段
				var form=btn.ownerCt.ownerCt;
				var keyField=form.fo_keyField;
				var title=form.title+Ext.getCmp(keyField).value;
				Ext.each(btn.ownerCt.items.items,function(b){
				 		b.hide();
				});
				var id1=btn.ownerCt.ownerCt.ownerCt.id;
				//调整宽度
				if(document.getElementById(id1)){
					document.getElementById(id1).style.height='auto';
					document.getElementById(id1+'-body').style.height='auto';
				}
				document.getElementById('form').style.height='auto';
				document.getElementById('form-body').style.height='auto';
				var item_f=Ext.getCmp('form').items.items;
				//赋值
				Ext.each(item_f,function(item){
					if(item.xtype=='combo'||item.xtype=='erpYnField'){
						document.getElementById(item.name+'-bodyEl').getElementsByTagName('input')[1].setAttribute("value",item.rawValue);
					}
				 	if(item.xtype=='textfield'||item.xtype=='numberfield'||item.xtype=='dbfindtrigger'){
				 		document.getElementById(item.name+'-bodyEl').getElementsByTagName('input')[0].setAttribute("value",item.value);
				 	}
				 	if(item.xtype=='datefield'){
				 		document.getElementById(item.name+'-bodyEl').getElementsByTagName('input')[0].setAttribute("value",item.rawValue);
				 	}
				 	if(item.xtype=='textareafield'){
				 		document.getElementById(item.name+'-bodyEl').getElementsByTagName('textarea')[0].innerHTML=item.rawValue;
				 	}
				 	if(item.xtype=='multifield'&&item.logic){
				 		document.getElementById(item.name+'-bodyEl').getElementsByTagName('input')[0].setAttribute("value",item.value);
				 		document.getElementById(item.logic+'-bodyEl').getElementsByTagName('input')[0].setAttribute("value",Ext.getCmp(item.logic).value);
				 	}
				 });
				 if(form.xtype=='erpFormPanel2'){
					 var win_print=window.open(basePath+"jsps/opensys/print.jsp?title="+ title, "_blank",'');	
				 }else{
				 	var win_print=window.open(basePath+"jsps/print.jsp?title="+ title, "_blank",'');	
				 }
			}
		}
	});