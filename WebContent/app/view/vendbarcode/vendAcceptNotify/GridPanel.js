	Ext.define("erp.view.vendbarcode.vendAcceptNotify.GridPanel",{
	extend : 'Ext.grid.Panel',
	alias : "widget.erpAcceptNotifyGrid",
	id : "gridAccept",
	emptyText : $I18N.common.grid.emptyText,
    autoScroll : true,
    columnLines : true,
    requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
    bbar: {xtype: 'erpToolbar', dock: 'bottom', enableAdd: true, enableDelete: true, enableCopy: true, enablePaste: true, enableUp: true, enableDown: true},
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    detno: 'and_detno',
    initComponent :function () {
       gridCondition = getUrlParam('gridCondition');
       formCondition = getUrlParam('formCondition');
       if(gridCondition && formCondition){
			this.getData();
		}
       this.callParent(arguments);
    },
    store: Ext.create('Ext.data.Store', {
    	storeId : 'myStore',
    	fields: [{
        	name:'and_id',
        	type:'number'
        }, {
        	name:'and_anid',
        	type:'number'
        },{
        	name:'and_detno',
        	type:'number'
        },{
        	name:'and_ordercode',
        	type:'string'
        },{
        	name:'and_orderdetno',
        	type:'number'
        },{
        	name:'and_prodcode',
        	type:'string'
        },{
        	name:'pr_detail',
        	type:'string'
        },{
        	name:'pr_spec',
        	type:'string'
        },{
        	name:'pr_unit',
        	type:'string'
        },{
        	name:'and_inqty',
        	type:'number'
        },{
        	name:'and_barqty',
        	type:'number'
        },{
        	name:'and_beipin',
        	type:'number'
        },{
        	name:'and_remark',
        	type:'string'
        },{
        	name:'pr_brand',
        	type:'string'
        },{
        	name:'pr_orispeccode',
        	type:'string'
        }],
	}),
    iconCls: 'icon-grid',
    frame: true,
    /*readOnly:true,*/
	fieldStyle:'background:#e0e0e0;',
    bodyStyle:'background-color:#f1f1f1;',
	columns: [{
        header: '从表ID',
        width: 0,
        hidden:true,
        dataIndex: 'and_id',
    },{
        header: '关联主表字段',
        width: 0,
        hidden:true,
        dataIndex: 'and_anid'
    },{
        text: '序号',
        width: 40,
        align:'center',
        dataIndex: 'and_detno',
        sortable: true
    },
    {
    	style :"text-align:center",
		text: '采购单号',
        width: 135,
        align:'left',
        dataIndex: 'and_ordercode',
        sortable: true,
    },
    {
    	style :"text-align:center",
		text: '采购序号',
        width: 70,
        align:'left',
        dataIndex: 'and_orderdetno',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '物料编号',
        width: 135,
        align:'left',
        dataIndex: 'and_prodcode',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '名称',
        width: 150,
        align:'left',
        dataIndex: 'pr_detail',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '规格',
        width: 225,
        align:'left',
        dataIndex: 'pr_spec',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '单位',
        width: 75,
        align:'left',
        dataIndex: 'pr_unit',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '品牌',
        width: 75,
        align:'left',
        dataIndex: 'pr_brand',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '原厂型号',
        width: 75,
        align:'left',
        dataIndex: 'pr_orispeccode',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '采购数量',
        width: 80,
        align:'left',
        dataIndex: 'and_inqty',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '已生成条码数量',
        width: 110,
        align:'left',
        dataIndex: 'and_barqty',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '备品数量',
        width: 110,
        align:'left',
        dataIndex: 'and_beipin',
        sortable: true,
    },{
    	style :"text-align:center",
		text: '备注',
        width: 260,
        align:'left',
        dataIndex: 'and_remark',
        sortable: true,
        editor : {
    		 xtype : 'textfield',
			}
    }],
    getData:function(){
		var me = this;
		//从url解析参数
		if(gridCondition != null && gridCondition != ''){
			gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=").replace(/\'/g,"");
		}
		me.FormUtil.getFieldsValues("acceptnotifydetail left join product on pr_code = and_prodcode",
				'and_id,and_anid, and_detno, and_ordercode, and_orderdetno, and_prodcode, pr_detail, pr_spec, pr_unit, and_inqty,and_barqty,and_beipin, and_remark,pr_orispeccode,pr_brand', gridCondition+" order by and_detno asc", [], function(data){
			var datas = Ext.JSON.decode(data), _datas = [];
			if(datas.length > 0) {
				var keys = Ext.Object.getKeys(datas[0]);
				Ext.Array.each(datas, function(d){
					var obj = {};
					Ext.Array.each(keys, function(key){
						obj[key.toLowerCase()] = d[key];
					});
					_datas.push(obj);
				});
			}
				store = me.getStore();
			 for(var i=0;i<store.data.items.length;i++){
				 Ext.Array.each(me.getStore().data.items,function(item){});
			   }
			_datas.length > 0 && me.store.loadData(_datas);
			me.store.each(function(){
				this.dirty = false;
			});
		});
	
	}
});