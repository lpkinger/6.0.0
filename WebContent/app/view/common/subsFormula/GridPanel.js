	Ext.define("erp.view.common.subsFormula.GridPanel",{
	extend : 'Ext.grid.Panel',
	alias : "widget.subsGridPanel",
	id : "grid",
	emptyText : $I18N.common.grid.emptyText,
    autoScroll : true,
    columnLines : true,
    bbar: {xtype: 'erpToolbar', dock: 'bottom', enableAdd: true, enableDelete: true, enableCopy: true, enablePaste: true, enableUp: true, enableDown: true},
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    detno: 'detno_',
    initComponent :function () {
       gridCondition = getUrlParam('gridCondition');
       this.getData();
       this.callParent(arguments);
    },
    store: Ext.create('Ext.data.Store', {
    	fields: [{
        	name:'formula_id_',
        	type:'number'
        }, {
        	name:'detno_',
        	type:'number'
        },{
        	name:'det_id_',
        	type:'number'
        },{
        	name:'field_',
        	type:'string'
        },{
        	name:'description_',
        	type:'string'
        },{
        	name:'width_',
        	type:'number'
        },{
        	name:'type_',
        	type:'string'
        },{
        	name:'format_',
        	type:'string'
        },{
        	name:'sum_',
        	type:'number'
        }],
         proxy: {
                  type: 'ajax',
                  async: false,
                  url : basePath + "common/charts/getSubsFormulaDet.action",
                  reader: {
                      idProperty:'nameEn',
                      type: 'json',
                      root: 'formulaDets'
                 }
		},
         autoLoad:false  
	}),
    iconCls: 'icon-grid',
    frame: true,
    bodyStyle:'background-color:#f1f1f1;',
	columns: [{
        header: '订阅项ID',
        width: 100,
        hidden:true,
        dataIndex: 'formula_id_'
    },{
        text: '序号',
        width: 40,
        align:'center',
        dataIndex: 'detno_',
        sortable: true
    },{
        header: 'ID',
        hidden:true,
        dataIndex: 'det_id_',
        sortable: true
    },{
        header: '<div style="text-align:center">字段</div>',
        width: 125,
        align:'left',
        dataIndex: 'field_',
        sortable: true,
        editor : {
    		xtype : 'textfield'
			}
    },{
        header: '<div style="text-align:center">列描述</div>',
        width: 251,
        align:'left',
        dataIndex: 'description_',
        sortable: true,
         editor : {
    		xtype : 'textfield'
			}
    },{
        header: '列宽',
        width: 100,
        style:'text-align:center',
        align:'right',
        dataIndex: 'width_',
        sortable: true,
         editor : {
    		xtype : 'textfield'
			}
    },{
        header: '<div style="text-align:center">字段类型</div>',
        width: 100,
        align:'left',
        dataIndex: 'type_',
        sortable: true,
         editor : {
    		xtype : 'combo',
			displayField:'name',			
			valueField: 'value',       
    		store: Ext.create('Ext.data.Store', {
                    fields : ['name', 'value'],
                    data   : [
                        {name : '文本类型',   value:'text'},
                        {name : '数字类型',   value:'number'}
                    ]
             })
             },
              renderer : function(val, meta, record) {
							var v1 = record.get('type_');
							var v;
							if (v1 == 'number') {
								v = '数字类型';
							} else {
								v = '文本类型';
							}
							return v;
						}
		
    },{
        header: '<div style="text-align:center">格式化方式</div>',
        width: 150,
        align:'left',
        dataIndex: 'format_',
        sortable: true,
         editor : {
    		xtype : 'textfield'
			}
    },{
        header: '<div style="text-align:center">是否需要合计</div>',
        width: 150,
        align:'left',
        dataIndex: 'sum_',
        sortable: true,
        editor : {
    		xtype : 'combo',						
				displayField:   'name',			
 				valueField: 'value',       
        		store: Ext.create('Ext.data.Store', {
                        fields : ['name', 'value'],
                        data   : [
                            {name : '是',   value:-1},
                            {name : '否',   value:0}
                        ]
             })
          },
       renderer : function(val, meta, record) {
							var v1 = record.get('sum_');
							var v;
							if (v1 < 0) {
								v = '是';
							} else {
								v = '否';
							}
							return v;
						}

    }],
    plugins:[  
         Ext.create('Ext.grid.plugin.CellEditing',{  
         clicksToEdit:1 //设置单击单元格编辑  
		})  
     ], 
    getData:function(){
		var me = this;
		//从url解析参数
		if(gridCondition != null && gridCondition != '')
			gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=").replace(/\'/g,"");
	}
});