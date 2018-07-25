Ext.define('erp.view.ma.jprocess.AutoJprocess',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id: 'Viewport',
				layout: 'auto',
				autoScroll: true,
				xtype: 'panel',
				style: {
					background: '#FFFFFF'
				},
				items: [{
					id: 'app-header',
					xtype: 'box',
					height: 5,
					style: 'background: #D4D4D4;color: #596F8F;font-size: 16px;font-weight: 200;padding: 5px 5px;text-shadow: 0 1px 0 #fff'
				},{
					xtype: 'toolbar',
					id: 'currentNodeToolbar',
					layout: {
						type: 'hbox',
						align: 'right'
					},
					bodyStyle: {
						background: "#D4D4D4",
						border: 'none'
					},
					style: {
						background: "#D4D4D4",
						border: 'none'
					},
					items: [
					        '->', {
					        	xtype: 'tbtext',
					        	id: 'label1',
					        	text: '<span style="font-weight: bold !important;font-size:18px">'+title+'</span>'
					        },'->']

				},{
					xtype:'tabpanel',
					bodyStyle:{
						border:'none'
					},
					bodyBorder:false,
					items: [{
						title:'基本信息',
						layout:'anchor',
						iconCls: 'main-msg',
						items:[{
							xtype: 'erpFormPanel',
							_noc:1,
							height:window.innerHeight*0.85,
							realCaller:CodeCaller,
							NoButton:true,
							saveUrl: 'common/saveAutoJprocess.action?_noc=1&caller='+CodeCaller,
							deleteUrl: 'common/deleteAutoJprocess.action?_noc=1&caller='+CodeCaller,
							updateUrl: 'common/updateAutoJprocess.action?_noc=1&caller='+CodeCaller,
							auditUrl: 'common/auditAutoJprocess.action?caller='+CodeCaller,
							resAuditUrl: 'common/resAuditAutoJprocess.action?caller='+CodeCaller,
							submitUrl: 'common/submitAutoJprocess.action?caller='+CodeCaller+"&_noc=1",
							resSubmitUrl: 'common/resSubmitAutoJprocess.action?caller='+CodeCaller+"&_noc=1",
							getIdUrl: 'common/getId.action?seq=JPROCESSTEMPLATE_SEQ',//存在同一张表公用一个序列号
							keyField: 'ap_id',
							statuscodeField: 'ap_statuscode',
							codeField:'ap_code',
							params:params
						}]
					},{
						title:'审批内容',
						layout:'anchor',
						iconCls:'x-button-icon-content',
						height:window.innerHeight*0.85,
						items:[{
							xtype:'htmleditor',
							anchor:'100% 100%'
						}],						
						id:'clobtext'
					},{
						title:'审批流程',
						height:window.innerHeight*0.85,
						iconCls:'x-button-icon-install',
						id:'workflow'
					}],
					dockedItems: [{
						xtype: 'toolbar',
						dock: 'bottom',
						items:  me.getButtons()
					}]
				}]			
			}]
		}); 
		me.callParent(arguments); 
	},
	getButtons:function (){
        var NoButton=getUrlParam('NoButton');
		var buttons=new Array();
		if(type!=1){
			buttons=['->',{
				xtype: 'erpSaveButton'
			},{
				xtype:'erpCloseButton'
			},'->'];
		}else 
			buttons=['->',{
				xtype: 'erpAddButton'
			},{
				xtype: 'erpUpdateButton',
				itemId:'update',
				hidden:true
			},{
				xtype: 'erpDeleteButton',				
				itemId:'delete',
				hidden:true
			},{
				xtype:'erpSubmitButton',
				itemId:'submit',
				hidden:true
			},{
				xtype:'erpResSubmitButton',
				itemId:'resSubmit',
				hidden:true
			},{
				xtype:'erpAuditButton',
				itemId:'audit',
				hidden:true
			},{
				xtype:'erpResAuditButton',
				itemId:'resAudit',
				hidden:true
			},{
				xtype:'erpCloseButton'
			},'->'];
		return buttons;
	}
});