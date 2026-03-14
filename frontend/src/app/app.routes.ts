import { Routes } from '@angular/router';
import { CreatePageComponent } from './pages/create/create-page.component';
import { ViewPageComponent } from './pages/view/view-page.component';
import {ListComponent} from "./pages/list/list.component";


export const routes: Routes = [
  { path: '', component: CreatePageComponent },
  { path: 'items/:hash', component: ViewPageComponent },
  { path: 'my-items', component: ListComponent },
  { path: '**', redirectTo: '' }
];