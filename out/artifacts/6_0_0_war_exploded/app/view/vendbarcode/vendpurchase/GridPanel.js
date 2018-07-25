	Ext.define("erp.view.vendbarcode.vendpurchase.GridPanel",{
	extend : 'Ext.grid.Panel',
	alias : "widget.erpvendPurchaseGrid",
	id : "gridVend",
	emptyText : $I18N.common.grid.emptyText,
    autoScroll : true,
    columnLines : true,
    requires: [ 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
  /*  bbar: {xtype: 'erpToolbar', dock: 'bottom', enableAdd: true, enableDelete: true, enableCopy: true, enablePaste: true, enableUp: true, enableDown: true},*/
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    detno: 'pd_detno',
    initComponent :function () {
       gridCondition = getUrlParam('gridCondition');
       if(gridCondition){
			this.getData();
		}
       this.callParent(arguments);
    },
    store: Ext.create('Ext.data.Store', {
    	storeId : 'myStore',
    	fields: [{
        	name:'Pd_id',
        	type:'number'
        }, {
        	name:'pd_puid',
        	type:'number'
        },{
        	name:'pd_detno',
        	type:'number'
        },{
        	name:'pd_prodcode',
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
        	name:'pr_purcunit',
        	type:'string'
        },{
        	name:'pd_qty',
        	type:'number'
        },{
        	name:'pd_ypurcqty',
        	type:'number'
        },{
        	name:'pd_delivery',
        	type:'date'
        },{
        	name:'pd_acceptqty',
        	type:'number'
        },{
        	name:'pd_ngacceptqty',
        	type:'number'
        },{
        	name:'pd_qtyreply',
        	type:'number'
        },{
        	name:'pd_deliveryreply',
        	type:'string'
        },{
        	name:'pd_replydetail',
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
    readOnly:true,
	fieldStyle:'background:#e0e0e0;',
    bodyStyle:'background-color:#f1f1f1;',
	columns: [{
        header: '从表ID',
        width: 0,
        hidden:true,
        dataIndex: 'Pd_id',
    },{
        header: '关联主表字段',
        width: 0,
        hidden:true,
        dataIndex: 'Pd_puid'
    },{
        text: '序号',
        width: 40,
        align:'center',
        dataIndex: 'pd_detno',
        sortable: true,
    },{
        /*header: '<div style="text-align:center">物料编号</div>',*/
    	style :"text-align:center",
		text: '物料编号',
        width: 125,
        align:'left',
        dataIndex: 'pd_prodcode',
        sortable: true,
        editor : {
    		xtype : 'textfield'
			}
    },{
    	style :"text-align:center",
		text: '名称',
        width: 150,
        align:'left',
        dataIndex: 'pr_detail',
        sortable: true,
        editor : {
    		xtype : 'textfield'
			}
    },{
    	style :"text-align:center",
		text: '规格',
        width: 225,
        align:'left',
        dataIndex: 'pr_spec',
        sortable: true,
        editor : {
    		xtype : 'textfield'
			}
    },{
    	style :"text-align:center",
		text: '单位',
        width: 75,
        align:'left',
        dataIndex: 'pr_unit',
        sortable: true,
        editor : {
    		xtype : 'textfield'
			}
    },{
    	style :"text-align:center",
		text: '采购单位',
        width: 100,
        align:'left',
        dataIndex: 'pr_purcunit',
        sortable: true,
        editor : {
    		xtype : 'textfield'
			}
    },{
    	style :"text-align:center",
		text: '品牌',
        width: 100,
        align:'left',
        dataIndex: 'pr_brand',
        sortable: true,
        editor : {
    		xtype : 'textfield'
			}
    },{
    	style :"text-align:center",
		text: '原厂型号',
        width: 125,
        align:'left',
        dataIndex: 'pr_orispeccode',
        sortable: true,
        editor : {
    		xtype : 'textfield'
			}
    },{
    	style :"text-align:center",
		text: '采购数量',
        width: 80,
        align:'left',
        dataIndex: 'pd_qty',
        sortable: true,
        editor : {
    		xtype : 'numberfield',
    		minValue: "0",
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			}
    },{
    	style :"text-align:center",
		text: '已转收料通知单数量',
        width: 135,
        align:'left',
        dataIndex: 'pd_ypurcqty',
        sortable: true,
        editor : {
    		xtype : 'numberfield',
    		minValue: "0",
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			}
    },{
    	style :"text-align:center",
		text: '需求日期',
        width: 100,
        align:'left',
        dataIndex: 'pd_delivery',
        sortable: true,
        format:"Y-m-d",
        xtype:"datecolumn"
    },{
    	style :"text-align:center",
		text: '验收数',
        width: 80,
        align:'left',
        dataIndex: 'pd_acceptqty',
        sortable: true,
        editor : {
    		xtype : 'numberfield',
    		minValue: "0",
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			}
    },{
    	style :"text-align:center",
		text: '不良入库数',
        width: 80,
        align:'left',
        dataIndex: 'pd_ngacceptqty',
        sortable: true,
        editor : {
    		xtype : 'numberfield',
    		minValue: "0",
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			}
    },{
    	style :"text-align:center",
		text: '回复数量',
        width: 80,
        align:'left',
        dataIndex: 'pd_qtyreply',
        sortable: true,
        editor : {
    		xtype : 'numberfield',
    		minValue: "0",
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			}
    },{
    	style :"text-align:center",
		text: '回复日期',
        width: 100,
        align:'left',
        dataIndex: 'pd_deliveryreply',
        sortable: true,
        editor : {
    		xtype : 'textfield'
			}
    },{
    	style :"text-align:center",
		text: '回复明细',
        width: 220,
        align:'left',
        dataIndex: 'pd_replydetail',
        sortable: true,
         editor : {
    		xtype : 'textfield'
			}
    }],
    getData:function(){
		var me = this;
		//从url解析参数
		if(gridCondition != null && gridCondition != ''){
			gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=").replace(/\'/g,"");
		}
		me.FormUtil.getFieldsValues("purchasedetail left join product on pr_code = pd_prodcode  left join scm_purchaseturnqty_view on v_pd_id = pd_id",
				'Pd_id,pd_puid,pd_detno,pd_prodcode,pr_detail,pr_spec,pr_unit,pr_purcunit,pd_qty,v_pd_turnacceptnotify pd_ypurcqty,pd_delivery,pd_acceptqty,pd_ngacceptqty,pd_qtyreply,pd_deliveryreply,pd_replydetail,pr_orispeccode,pr_brand', gridCondition+" order by pd_detno asc", [], function(data){
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