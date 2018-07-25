Ext.define('erp.view.core.button.LendTrim',{
	extend : 'Ext.Button',
	alias : 'widget.erpLendTrim',
	requires: ['erp.util.FormUtil'],
	iconCls : 'x-button-icon-check',
	text : $I18N.common.button.erpLendTrimButton,
	cls: 'x-btn-gray',
	width: 110,
	id: 'erpLendTrimButton',
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){
		this.callParent(arguments); 
	},
	handler : function(btn){
		var me = this;
		var grid = Ext.getCmp('batchDealGridPanel');
		var items = grid.getMultiSelected();
		var a=1;
		var type = "";
		var ob_id="";
		var tqty ="";
		var sa_code="";
		var ob_sadetno="";
		if(items[0].data.sa_code){
			var sacode = items[0].data.sa_code;
			var sadetno = items[0].data.ob_sadetno;
			var pr_code = items[0].data.pr_code;
			var condition = "(nvl(sa_code,' ')<>'"+items[0].data.sa_code+"' or nvl(ob_sadetno,0)<>"+items[0].data.ob_sadetno+") and pr_code='"+pr_code+"'";
			for(var i=0;i<items.length;i++){
				if(items[i].data.sa_code!=sacode||items[i].data.ob_sadetno!=sadetno||items[i].data.pr_code!=pr_code){
					a++;
				}
				type = type + "," + items[i].data.type;
				ob_id = ob_id + ","+items[i].data.ob_id;
				tqty = tqty + ","+items[i].data.ob_tqty;
				sa_code = sa_code + ","+items[i].data.sa_code;
				ob_sadetno = ob_sadetno + ","+items[i].data.ob_sadetno;
			}
		}else if(items[0].data.sf_code){
			var sfcode = items[0].data.sf_code;
			var sfdetno = items[0].data.ob_sfdetno;
			var pr_code = items[0].data.pr_code;
			var condition = "(nvl(sf_code,' ')<>'"+items[0].data.sf_code+"' or nvl(ob_sfdetno,0)<>"+items[0].data.ob_sfdetno+") and pr_code='"+pr_code+"'";
			for(var i=0;i<items.length;i++){
				if(items[i].data.sf_code!=sfcode||items[i].data.ob_sfdetno!=sfdetno||items[i].data.pr_code!=pr_code){
					a++;
				}
			}
		}else if(!items[0].data.sf_code&&!items[0].data.sa_code){
			showError("请勾选有销售订单或销售备货单的数据");
			return;
		}
		if(a>1){
			showError("只能勾选同销售订单、序号和物料或者同备货单号、序号和物料的数据");
			return;
		}
        	var url_ = basePath+"jsps/common/batchDeal.jsp?whoami=LendTrim!Deal&urlcondition="+condition+"&type="+type.substr(1)+"&id="+ob_id.substr(1)+"&tqty="+tqty.substr(1)+"&sa_code1="+sa_code.substr(1)+"&ob_sadetno1="+ob_sadetno.substr(1)+"&pr_code="+items[0].data.pr_code+"";
        	console.log(url_);
        	var panel = {
	    			title:'借调',
	    			tag : 'iframe',
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',
	    			html : '<iframe id="iframe_maindetail_'+caller+'" src="'+url_+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    			closable : true
	    	 };
	    	 me.FormUtil.openTab(panel);
	},
});