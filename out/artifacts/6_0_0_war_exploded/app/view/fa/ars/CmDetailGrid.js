/**
 * 
 */
Ext.define('erp.view.fa.ars.CmDetailGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.cmdetailgrid',
	layout : 'fit',
	id: 'cmdetailgrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'tb_code',
        	type: 'string'
        },{
        	name: 'tb_kind',
        	type: 'string'
        },{
        	name: 'tb_date',
        	type: 'string'
        },{
        	name: 'tb_aramount',
        	type: 'number'
        },{
        	name: 'tb_rbamount',
        	type: 'number'
        },{
        	name: 'tb_balance',
        	type: 'number'
        },{
        	name: 'tb_index',
        	type: 'number'
        }],
        data: []
    }),
    defaultColumns: [{
		dataIndex: 'tb_date',
		cls: 'x-grid-header-1',
		text: '日期',
		width: 200
	},{
		dataIndex: 'tb_kind',
		cls: 'x-grid-header-1',
		text: '单据类型',
		width: 120
	},{
		dataIndex: 'tb_code',
		cls: 'x-grid-header-1',
		text: '单据编号',
		width: 150
	},{
		dataIndex: 'tb_aramount',
		cls: 'x-grid-header-1',
		text: '应收金额',
		width: 120,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==1||record.data['tb_index']==3){
				if(val==0||val=='0'){
					return '';
				}else{
					return val;
				}
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_rbamount',
		cls: 'x-grid-header-1',
		text: '收款金额',
		width: 120,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==1||record.data['tb_index']==3){
				if(val==0||val=='0'){
					return '';
				}else{
					return val;
				}
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_balance',
		cls: 'x-grid-header-1',
		text: '余额',
		width: 120/*,
		renderer:function(val, meta, record){
			console.log(arguments);
			if(record.data['tb_index']==2){
				if(val==0||val=='0'){
					return '';
				}else{
					return val;
				}
			}else{
				return val;
			}
		}*/
	}],
	detailColumns: [],
    bodyStyle:'background-color:#f1f1f1;',
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
	initComponent : function(){
		this.columns = this.defaultColumns;
		this.callParent(arguments); 
	},
//	renderFun:function(){
//		console.log(arguments);
//	},
	viewConfig: { 
        getRowClass: function(record) { 
            return record.get('isCount') ? 'isCount' : null; 
        } 
    }
});