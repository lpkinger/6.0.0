Ext.define('erp.view.core.button.LendTrimmer',{
	extend : 'Ext.Button',
	alias : 'widget.erpLendTrimmer',
	requires: ['erp.util.FormUtil'],
	iconCls : 'x-button-icon-submit',
	text : $I18N.common.button.erpLendTrimmerButton,
	cls: 'x-btn-gray',
	width: 110,
	id: 'erpLendTrimmerButton',
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
		var pu_code="";
		var pu_detno="";
		if(items[0].data.sa_code){
			var sacode = items[0].data.sa_code;
			var sadetno = items[0].data.ob_sadetno;
			var pr_code = items[0].data.pr_code;
			var condition = "(nvl(sa_code,' ')<>'"+items[0].data.sa_code+"' or nvl(ob_sadetno,0)<>"+items[0].data.ob_sadetno+" ) and nvl(pr_code,' ')='"+items[0].data.pr_code+"'";
			for(var i=0;i<items.length;i++){
				if(items[i].data.sa_code!=sacode||items[i].data.ob_sadetno!=sadetno){
					a++;
				}
				type = type + "," + items[i].data.type;
				ob_id = ob_id + ","+items[i].data.ob_id;
				tqty = tqty + ","+items[i].data.ob_tqty;
				sa_code = sa_code + ","+items[i].data.sa_code;
				ob_sadetno = ob_sadetno + ","+items[i].data.ob_sadetno;
				pu_code = pu_code + ","+items[i].data.pu_code;
				pu_detno = pu_detno + ","+items[i].data.ob_pudetno;
			}
		}else if(!items[0].data.sa_code){
			showError("请勾选有销售订单的数据");
			return;
		}
		if(a>1){
			showError("只能勾选同销售订单、序号");
			return;
		}
		return Ext.create('Ext.window.Window',{
			width:1011,
			height:'95%',
			iconCls:'x-grid-icon-partition',
			title:'<h1>借调申请</h1>',
			layout:'fit',
			id:'win',
			items:[{
    			tag : 'iframe',
    			frame : true,
    			border : false,
    			layout : 'anchor',
    			iconCls : 'x-tree-icon-tab-tab',
    			html : '<iframe id="iframe_maindetail_'+caller+'" src="'+basePath+"/jsps/scm/sale/myBatchDeal.jsp?whoami=LendTrimd!Deal&urlcondition="+condition+"&sa_code1="+sa_code.substr(1)+"&type="+type.substr(1)+"&tqty="+tqty.substr(1)+"&id="+ob_id.substr(1)+"&ob_sadetno1="+ob_sadetno.substr(1)+"&pu_code="+pu_code.substr(1)+"&pu_detno1="+pu_detno.substr(1)+"&pr_code="+items[0].data.pr_code+""+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    			//closable : true
			}],
		}).show();
	},
});