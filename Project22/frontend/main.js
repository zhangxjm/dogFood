import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import { Button, Cell, CellGroup, Field, Form, NavBar, Tabbar, TabbarItem, Tab, Tabs, List, PullRefresh, Empty, Loading, Card, Tag, Badge, Divider, Popup, Picker, ActionSheet, Dialog, Notify } from 'vant'
import 'vant/lib/index.css'

const app = createApp(App)

app.use(router)

app.use(Button)
app.use(Cell)
app.use(CellGroup)
app.use(Field)
app.use(Form)
app.use(NavBar)
app.use(Tabbar)
app.use(TabbarItem)
app.use(Tab)
app.use(Tabs)
app.use(List)
app.use(PullRefresh)
app.use(Empty)
app.use(Loading)
app.use(Card)
app.use(Tag)
app.use(Badge)
app.use(Divider)
app.use(Popup)
app.use(Picker)
app.use(ActionSheet)
app.use(Dialog)
app.use(Notify)

app.mount('#app')
