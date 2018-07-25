Ext.define('erp.view.common.JProcess.ItemSelector', {
	extend: 'Ext.ux.form.MultiSelect',
	alias: ['widget.itemselectorfield', 'widget.itemselector'],
	alternateClassName: ['Ext.ux.ItemSelector'],
	requires: ['Ext.ux.layout.component.form.ItemSelector', 'Ext.button.Button'],
	hideNavIcons:false,
	buttons: ['top', 'up', 'add', 'remove', 'down', 'bottom'],
	buttonsText: {
		top: "最上",
		up: "向上",
		add: "添加",
		remove: "移除",
		down: "向下",
		bottom: "最下"
	},
	componentLayout: {
		type:'itemselectorfield',
		defaultHeight:window.innerHeight*0.7
	},
	initComponent: function(){
		var me = this;
		me.getStore(me);
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
	/*    defaultHeight:400,*/
	//fieldBodyCls: Ext.baseCSSPrefix + 'form-itemselector-body',
	getStore:function(field){
		Ext.Ajax.request({
			url : basePath + 'common/getPersonalProcessInfo.action',
			method : 'post',
			async:false,
			callback : function(options,success,response){	   		
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					field.store=Ext.create('Ext.data.ArrayStore', {
                	    data: localJson.data.all,
                	    fields: ['value','text'],
                	    sortInfo: {
                	           field: 'value',
                	           direction: 'ASC'
                	      }
                });
					field.value=localJson.data.owner;
				}
			}
		});
	},
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
			hideLabel: true,
			disabled: me.disabled
		},
		fromConfig = Ext.apply({
			listTitle: '未选择系统流程',
			height:window.innerHeight*0.7,
			store: Ext.create('Ext.data.Store', {model: me.store.model}), 
			listeners: {
				boundList: {
					itemdblclick: me.onItemDblClick,
					scope: me
				}
			}
		}, me.multiselects[0], commonConfig),
		toConfig = Ext.apply({
			listTitle: '我的流程',
			height:window.innerHeight*0.7,
			store: Ext.create('Ext.data.Store', {model: me.store.model}), //blank store to begin
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
					cls: baseCSSPrefix + 'form-itemselector-btn',
					iconCls: baseCSSPrefix + 'form-itemselector-' + name,
					scope: me
				});
				console.log('form-itemselector-' + name);
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

	onTopBtnClick : function() {
		var list = this.toField.boundList,
		store = list.getStore(),
		selected = this.getSelections(list),
		i = selected.length - 1,
		selection;


		store.suspendEvents();
		for (; i > -1; --i) {
			selection = selected[i];
			store.remove(selected);
			store.insert(0, selected);
		}
		store.resumeEvents();
		list.refresh();    
	},

	onBottomBtnClick : function() {
		var list = this.toField.boundList,
		store = list.getStore(),
		selected = this.getSelections(list),
		i = 0,
		len = selected.length,
		selection;

		store.suspendEvents();
		for (; i < len; ++i) {
			selection = selected[i];
			store.remove(selection);
			store.add(selection);
		}
		store.resumeEvents();
		list.refresh();
	},

	onUpBtnClick : function() {
		var list = this.toField.boundList,
		store = list.getStore(),
		selected = this.getSelections(list),
		i = 0,
		len = selected.length,
		selection,
		index;

		store.suspendEvents();
		for (; i < len; ++i) {
			selection = selected[i];
			index = Math.max(0, store.indexOf(selection) - 1);
			store.remove(selection);
			store.insert(index, selection);
		}
		store.resumeEvents();
		list.refresh();
	},

	onDownBtnClick : function() {
		var list = this.toField.boundList,
		store = list.getStore(),
		selected = this.getSelections(list),
		i = 0,
		len = selected.length,
		max = store.getCount(),
		selection,
		index;

		store.suspendEvents();
		for (; i < len; ++i) {
			selection = selected[i];
			index = Math.min(max, store.indexOf(selection) + 1);
			store.remove(selection);
			store.insert(index, selection);
		}
		store.resumeEvents();
		list.refresh();
	},

	onAddBtnClick : function() {
		var me = this,
		fromList = me.fromField.boundList,
		selected = this.getSelections(fromList);

		fromList.getStore().remove(selected);
		this.toField.boundList.getStore().add(selected);
	},

	onRemoveBtnClick : function() {
		var me = this,
		toList = me.toField.boundList,
		selected = this.getSelections(toList);

		toList.getStore().remove(selected);
		this.fromField.boundList.getStore().add(selected);
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
	 }

});

