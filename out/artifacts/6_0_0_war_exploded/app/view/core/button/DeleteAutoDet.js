/**
 * 报价公共询价单明细删除按钮
 */	
Ext.define('erp.view.core.button.DeleteAutoDet',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeleteAutoDetButton',
		iconCls: 'x-button-icon-delete',
		id: 'deleteAutoDet',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDeleteAutoDetButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners:{
			click:function(){
				var grid = Ext.getCmp('grid');
				if(grid){
					record = grid.selModel.lastSelected ;
					if(!record){
						showError('请先选择明细行!');
					}
				}
				var id_id = record.data['id_id'];
				warnMsg("删除后将返回到已报价询价,是否确定删除?", function(b){
					if(b=='yes'){
						Ext.Ajax.request({
							url: basePath + 'scm/purchase/deleteAutoDet.action',
							params: {
								caller : caller,
								id : id_id
							},
							callback: function(opt, s, r) {
								var rs = Ext.decode(r.responseText);
								if(rs.exceptionInfo) {
									showError(rs.exceptionInfo);
								} else{
									grid.store.remove(record);
					    			showMessage("提示", "删除明细成功");
								}
							}
						});
					}
				})
			}
		}
	});