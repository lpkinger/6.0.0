/**
 * 查看图片按钮  目前用于销售订单查看物料资料图片
 */	
Ext.define('erp.view.core.button.LookPhoto',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLookPhotoButton',
		iconCls: 'x-button-icon-pic',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpLookPhotoButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(btn){
			var grid = Ext.getCmp('grid');
			if(grid&&grid.selModel.selected.items.length>0){
				var rec = grid.selModel.selected.items[0];
				var url = rec.get('pr_photourl');
				if(url&&url!=''){
					var src = 'common/download.action?path=' + url.replace(/\+/g, '%2B');
					var win =new Ext.window.Window({
						title: '<span style="color:#115fd8;">查看图片</span>',
						draggable:true,
						height: '68%',
						width: '70%',
						resizable:false,
				   		modal: true,
				   		maximizable : true,
				   		layout:'border',
					   	items: [{
					   		readOnly:true,
					   		enableAlignments :false,
					   		enableColors :false,
					   		enableSourceEdit :false,
					   		enableFont:false,
					   		enableFontSize :false,
					   		enableFormat:false, 
					   		enableLinks :false,
					   		enableLists :false,
					   		value:'<img src="'+basePath+src+'" scrolling="auto" style="width:100%;height:100%;">',
					   		xtype: 'htmleditor',
							cls:'x-pf-htmleditor',
							layout: 'fit',
							region: 'center',
							autoScroll:true
						}]
					});
					win.show();	
				}else{
					showMessage('该物料未导入图片或配置错误')
				}
			}else{
				showMessage('未选择明细数据')
			}
		}
	});