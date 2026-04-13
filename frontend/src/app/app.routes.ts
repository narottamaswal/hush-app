import {Routes} from '@angular/router';
import {CreatePageComponent} from './pages/create/create-page.component';
import {ViewPageComponent} from './pages/view/view-page.component';
import {ListComponent} from "./pages/list/list.component";
import {authGuard} from "./guards/auth.guard";


export const routes: Routes = [
    {path: '', component: CreatePageComponent, pathMatch: 'full'},
    {path: 'items/:hash', component: ViewPageComponent},
    {path: 'my-items', component: ListComponent, pathMatch: 'full',canActivate: [authGuard]},
    {path: '**', redirectTo: ''}
];