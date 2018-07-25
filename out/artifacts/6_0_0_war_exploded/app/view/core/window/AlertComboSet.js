Ext.define('erp.view.core.window.AlertComboSet', {
	extend: 'Ext.window.Window',
	alias: 'widget.alertComboSet',
	width: '60%',
	height: '60%',
	autoShow: true,
	layout: 'border',
	buttonAlign : 'center',
	title:'<h1>下拉框、复选框配置</h1>',
	index:'',
	values:'',
	items: [{
		region:'center',
		xtype: 'grid',
		id:'alertcombogrid',
		columnLines:true,
		bodyStyle: 'background-color:#f1f1f1;',
		plugins: Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToEdit: 1
		}),
		listeners:{
			itemclick:function(selModel, record,e,index){			    
				var grid=selModel.ownerCt;
				//Ext.getCmp('deletecombo').setDisabled(false);
				if(index.toString() == 'NaN'){
					index = '';
				}
				if(index == grid.store.data.items.length-1){//如果选择了最后一行
					var items=grid.store.data.items;
					var detno=Math.ceil(items[index].data.detno);
					for(var i=0;i<10;i++){
						detno++;
						var o = new Object();
						o.display=null;
						o.value=null;
						o.detno=detno;
						grid.store.insert(items.length, o);
						items[items.length-1]['index'] = items.length-1;
					}
				}
			}	
		},
		columns:[{
			cls : "x-grid-header-1",
			text:'序号',
			dataIndex: 'detno',
			xtype: 'numbercolumn',
			flex: 0.25,
			format: '0,000',
			renderer: function(val, meta, record){
				if(!val){
					val=0;
				}
				return Math.ceil(val);						  
			}
		},{
			cls : "x-grid-header-1",
			text: '显示值',
			dataIndex: 'display',
			flex: 1,
			editor: {
				format:'',
				xtype: 'textfield',
				regex: /^[^;:]+$/,
				regexText: '不能输入分号和冒号'
			}
		},{
			cls : "x-grid-header-1",
			text: '实际值',
			dataIndex: 'value',
			flex: 1,
			editor: {
				format:'',
				xtype: 'textfield',
				regex: /^[^;:]+$/,
				regexText: '不能输入分号和冒号'
			}
		}]
	}],
	buttons:['->',{
		xtype:'button',
		text: '确认',
		iconCls: 'x-button-icon-save',
		cls: 'x-btn-gray',
		width: 60,
		style: {
			marginLeft: '10px'
		},
		handler:function(){
			var statusCode = Ext.getCmp('ai_statuscode').value;
			if(statusCode=='ENTERING') {
				me.saveCombo();
			}
			me.close();
		}
	},{
		xtype:'button',
		text: $I18N.common.button.erpDeleteButton,
		iconCls: 'x-button-icon-delete',
		cls: 'x-btn-gray',
		id:'deletecombo',
		width: 60,
		style: {
			marginLeft: '10px'
		},
		handler:function(){
			me.deleteCombo();
		}

	},{
		xtype:'button',
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
		cls: 'x-btn-gray',
		width: 60,
		style: {
			marginLeft: '10px'
		},
		handler:function(btn){      	
			me.close();
		} 
	},'->'],
	initComponent: function() {
		me=this;
		this.callParent(arguments);
		me.loadData();
	},
	loadData: function(){
		var grid = Ext.getCmp('alertcombogrid');
		var combodata = new Array();
		var values = me.values;
		if(values==''){
			me.add10EmptyData(combodata);
		}else{
			var detno = 1;
			Ext.Array.each(values.split(';'),function(name, index){
				if(name!=null&&name!=''){
					var d = name.split(':');
					var o = new Object();
					o.display=d[0];
					o.value=d[1];
					o.detno=detno++;
					combodata.push(o);
				}
			})
		}
		grid.getStore().loadData(combodata);
	},
	saveCombo: function(){
		var grid = Ext.getCmp('alertcombogrid');
		var data = grid.getStore().data.items;
		var values = '';
		Ext.Array.each(data,function(name,index){
			if(name.data['value']!=null&&name.data['value']!=''){
				if(name.data['display']==null||name.data['display']==''){
					name.data['display']=name.data['value'];
				}
				values = values + name.data['display'] + ':' + name.data['value'] + ';'
			}
		})
		//把值保存在项目参数设置中
		var argsgrid = Ext.getCmp('grid');
		var store = argsgrid.getStore();
		var rowStore = store.getAt(me.index);
		if(rowStore){
			rowStore.set('aa_values',values);
		}
	},
	deleteCombo: function(){
		var grid = Ext.getCmp('alertcombogrid');
		grid.getStore().remove(grid.getSelectionModel().getSelection());
	},
	add10EmptyData: function(data){
		for(var i=0;i<10;i++){
			var o = new Object();
			o.value=null;
			o.display=null;
			o.detno=i + 1;
			data.push(o);
		}
	}
});