/**
 * 导出矩阵bOM
 */	
Ext.define('erp.view.core.button.ExportBomCheckMsg',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpExportBomCheckMsgButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'ExportBomCheckMsg',
    	text: '导出全部详情',
    	style: {
    		marginLeft: '10px'
        },
        width: 115,
        listeners:{
        	afterrender:function(btn){
        		btn.setDisabled(true);
        	}
        },
        handler :function(){
    				var form =Ext.getCmp('formPanel');
    				var caller='BomCheck';
    				var checkbomid = Ext.getCmp('checkbombtn').haveChecked;
    				var bomId=Ext.getCmp(form.keyField).value;
    				if(!checkbomid){
    					showError('请先输入BOMID!');
    					return false;
    				}else if(bomId != checkbomid){
    					showError('请先检查该BOMID，再进行导出!');
    					return false;
    				}else{
    					window.location.href=basePath+"excel/exportBOMCheckMessageExcel.action?caller="+caller+"&bomId="+bomId;
    				}
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});