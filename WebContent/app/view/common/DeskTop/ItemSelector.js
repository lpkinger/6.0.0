Ext.define('erp.view.common.DeskTop.ItemSelector', {
	extend: 'Ext.ux.form.MultiSelect',
	alias: ['widget.itemselectorfield', 'widget.itemselector'],
	alternateClassName: ['Ext.ux.ItemSelector'],
	requires: ['Ext.ux.layout.component.form.ItemSelector', 'Ext.button.Button'],
	hideNavIcons:false,
	buttons: ['add', 'remove','addAll','removeAll'],
	buttonsText: {
		add: "添加&nbsp;&nbsp;>>",
		remove: "<<&nbsp;&nbsp;移除",
		addAll: "全部添加",
		removeAll: "全部移除"
	},
	componentLayout: {
		type:'itemselectorfield',
		defaultHeight:window.innerHeight*0.7
	},
	store:Ext.create('Ext.data.ArrayStore', {
	    data: [],
	    fields: ['id','text'],
	    sortInfo: {
	           field: 'value',
	           direction: 'ASC',
	           idProperty:'value'
	      }
    }),
	initComponent: function(){
		var me = this;
		me.bindStore(me.store, true);
		if (me.store.autoCreated) {
			me.valueField = me.displayField = 'field1';
			if (!me.store.expanded) {
				me.displayField = 'field2';
			}
		}

		if (!Ext.isDefined(me.valueField)) {
			me.valueField = me.displayField;
		}

		me.callParent();
	},
	multiselects: [],
	bindStore: function(store, initial) {
		var me = this,
		toField = me.toField,
		fromField = me.fromField,
		models;

		me.callParent(arguments);

		if (toField) {
			// Clear both field stores
			toField.store.removeAll();
			fromField.store.removeAll();
			models = [];
			me.store.each(function(model) {
				models.push(model.copy(model.getId()));
			});
			fromField.store.add(models);
		}
	},
	onRender: function(ct, position) {
		var me = this,
		baseCSSPrefix = Ext.baseCSSPrefix,
		ddGroup = 'ItemSelectorDD-' + Ext.id(),
		commonConfig = {
			displayField: me.displayField,
			valueField: me.valueField,
			dragGroup: ddGroup,
			dropGroup: ddGroup,
			flex: 1,
			hideLabel: false,
			disabled: me.disabled
		},
		fromConfig = Ext.apply({
			height:window.innerHeight*0.7,
			store: Ext.create('Ext.data.Store', {model: me.store.model}), 
			listeners: {
				boundList: {
					itemdblclick: me.onItemDblClick,
					beforedrop:function( node, data, overModel, dropPosition, dropFunction,  eOpts){
					var bool=data.records[0].data.remove;
					if(!bool){return false;}
					},
					scope: me
				}
			}
		}, me.multiselects[0], commonConfig),
		toConfig = Ext.apply({
			height:window.innerHeight*0.7,
			store: Ext.create('Ext.data.Store', {model: me.store.model}),
			listeners: {
				boundList: {
					itemdblclick: me.onItemDblClick,					
					scope: me
				},
				change: me.onToFieldChange,
				scope: me
			}
		}, me.multiselects[1], commonConfig),
		fromField = Ext.widget('multiselect', fromConfig),

		toField = Ext.widget('multiselect', toConfig),
		innerCt,
		buttons = [];
		Ext.ux.form.MultiSelect.superclass.onRender.call(me, ct, position);

		me.fromField = fromField;
		me.toField = toField;

		if (!me.hideNavIcons) {
			Ext.Array.forEach(me.buttons, function(name) {
				buttons.push({
					xtype: 'button',
					tooltip: me.buttonsText[name],
					handler: me['on' + Ext.String.capitalize(name) + 'BtnClick'],
					text:me.buttonsText[name],
					cls:'button1 pill',
					scope: me
				});
				buttons.push({xtype: 'component', height: 3, width: 1, style: 'font-size:0;line-height:0'});
			});
		}

		innerCt = me.innerCt = Ext.widget('container', {
			renderTo: me.bodyEl,
			layout: {
				type: 'hbox',
				align: 'middle'
			},
			items: [
			        me.fromField,
			        {
			        	xtype: 'container',
			        	margins: '0 10',
			        	items: buttons
			        },
			        me.toField
			        ]
		});
		innerCt.ownerCt = me;
		me.bindStore(me.store);
		me.setRawValue(me.rawValue);
	},

	onToFieldChange: function() {
		this.checkChange();
	},

	getSelections: function(list){
		var store = list.getStore(),
		selections = list.getSelectionModel().getSelection(),
		i = 0,
		len = selections.length;

		return Ext.Array.sort(selections, function(a, b){
			a = store.indexOf(a);
			b = store.indexOf(b);

			if (a < b) {
				return -1;
			} else if (a > b) {
				return 1;
			}
			return 0;
		});
	},
	onAddBtnClick : function() {//添加
		var me = this,
		fromList = me.fromField.boundList,
		selected = this.getSelections(fromList),needto=new Object(),addArr=new Array();
		var toStore=this.toField.boundList.getStore();
		Ext.Array.each(toStore.data.items,function(item){
			needto[item.data.id]=item.data;
		});
		Ext.Array.each(selected,function(item){
			needto[item.data.id]=item.data;
		});
		var keys=Ext.Object.getKeys(needto);
		Ext.each(keys, function(k){
			addArr.push(needto[k]);
		});
		fromList.getStore().remove(selected);
		this.toField.boundList.getStore().loadData(addArr);
	},
	onAddAllBtnClick : function() {//全部添加
		var me = this,
		fromList = me.fromField.boundList,
		toValues=this.toField.boundList.getStore().data,
		needto=new Object(),
		addArr=new Array();
		var toStore=this.toField.boundList.getStore();
		Ext.Array.each(toStore.data.items,function(item){
			needto[item.data.id]=item.data;
		});
		Ext.Array.each(fromList.getStore().data.items,function(item){
			needto[item.data.id]=item.data;
		});
		var keys=Ext.Object.getKeys(needto);
		Ext.each(keys, function(k){
			addArr.push(needto[k]);
		});
		this.toField.boundList.getStore().loadData(addArr);
		fromList.getStore().removeAll();	
	},
	onRemoveBtnClick : function() {//移除
		var me = this,
		toList = me.toField.boundList,
		selected = this.getSelections(toList);
		if(selected.length>0){
			for(var i=0;i<selected.length;i++){
				if(selected[i].data.remove){
					toList.getStore().remove(selected[i]);
					me.fromField.boundList.getStore().add(selected[i]);
				}
			}
			}
		
	},
	onRemoveAllBtnClick : function() {//全部移除
		var me = this,
		toList = me.toField.boundList;
		var data_r=new Array();
		Ext.Array.each(toList.getStore().data.items,function(item){
			if(item.data.remove){
				data_r.push(item);
			}
		});
		me.fromField.boundList.getStore().add(data_r);
		toList.getStore().remove(data_r);	
	},
	onItemDblClick : function(view) {
		var me = this;
		if (view == me.toField.boundList){
			me.onRemoveBtnClick();
		}
		else if (view == me.fromField.boundList) {
			me.onAddBtnClick();
		}
	},

	setRawValue: function(value) {
		var me = this,
		Array = Ext.Array,
		toStore, fromStore, models;

		value = Array.from(value);
		me.rawValue = value;

		if (me.toField) {
			toStore = me.toField.boundList.getStore();
			fromStore = me.fromField.boundList.getStore();

			// Move any selected values back to the fromField
			fromStore.add(toStore.getRange());
			toStore.removeAll();

			// Move the new values over to the toField
			models = [];
			Ext.Array.forEach(value, function(val) {
				var undef,
				model = fromStore.findRecord(me.valueField, val, undef, undef, true, true);
				if (model) {
					models.push(model);
				}
			});
			fromStore.remove(models);
			toStore.add(models);
		}

		return value;
	},

	getRawValue: function() {
		var me = this,
		toField = me.toField,
		rawValue = me.rawValue;

		if (toField) {
			rawValue = Ext.Array.map(toField.boundList.getStore().getRange(), function(model) {
				return model.get(me.valueField);
			});
		}

		me.rawValue = rawValue;
		return rawValue;
	},

	/**
	 * @private Cascade readOnly/disabled state to the sub-fields and buttons
	 */
	 updateReadOnly: function() {
		 var me = this,
		 readOnly = me.readOnly || me.disabled;

		 if (me.rendered) {
			 me.toField.setReadOnly(readOnly);
			 me.fromField.setReadOnly(readOnly);
			 Ext.Array.forEach(me.innerCt.query('button'), function(button) {
				 button.setDisabled(readOnly);
			 });
		 }
	 },

	 onDisable: function(){
		 this.callParent();
		 var fromField = this.fromField;

		 // if we have one, we have both, they get created at the same time    
		 if (fromField) {
			 fromField.disable();
			 this.toField.disable();
		 }
	 },

	 onEnable: function(){
		 this.callParent();
		 var fromField = this.fromField;

		 // if we have one, we have both, they get created at the same time    
		 if (fromField) {
			 fromField.enable();
			 this.toField.enable();
		 }
	 },

	 onDestroy: function() {
		 Ext.destroyMembers(this, 'innerCt');
		 this.callParent();
	 },
	 getModelData:function(){
		 var me = this,
			toField = me.toField,arr=new Array();

			if (toField) {
				rawValue = Ext.Array.map(toField.boundList.getStore().getRange(), function(model) {
					arr.push(Ext.JSON.encode(model.data));
				});
			}
		return arr;
	 }

});

